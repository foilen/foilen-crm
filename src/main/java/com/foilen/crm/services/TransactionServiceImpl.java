package com.foilen.crm.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.exception.CrmException;
import com.foilen.crm.web.model.CreatePayment;
import com.foilen.crm.web.model.TransactionList;
import com.foilen.crm.web.model.TransactionWithBalance;
import com.foilen.smalltools.email.EmailBuilder;
import com.foilen.smalltools.email.EmailService;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.DirectoryTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.PriceFormatTools;
import com.foilen.smalltools.tools.ResourceTools;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
@Transactional
public class TransactionServiceImpl extends AbstractApiService implements TransactionService {

    @Autowired
    private Configuration freemarkerConfiguration;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private TransactionDao transactionDao;

    @Value("${crm.company}")
    private String company;
    @Value("${crm.emailTemplateDirectory:#{null}}")
    private String emailTemplateDirectory;
    @Value("${crm.mailFrom}")
    private String mailFrom;

    @Override
    public FormResult create(String userId, CreatePayment form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreatePaymentOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "paymentType", form.getPaymentType());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        String paymentMessage = messageSource.getMessage("transaction.create.paymentDescription", new Object[] { form.getPaymentType() }, client.getLangAsLocale());

        Transaction entity = JsonTools.clone(form, Transaction.class);
        entity.setClient(client);
        entity.setDescription(paymentMessage);
        entity.setPrice(entity.getPrice() * -1);
        transactionDao.save(entity);

        return formResult;

    }

    @Override
    public Transaction createTransaction(Client client, List<Item> items, String invoicePrefix, AtomicLong nextInvoiceSuffix) {

        // Find the next available id
        String invoiceId = null;
        while (invoiceId == null) {
            invoiceId = invoicePrefix + "-" + nextInvoiceSuffix.getAndIncrement();
            logger.info("Checking if invoice id {} is available", invoiceId);
            if (transactionDao.findByInvoiceId(invoiceId) != null) {
                invoiceId = null;
            }
        }

        logger.info("Using invoice id {}", invoiceId);

        // Create the transaction
        String description = messageSource.getMessage("transaction.create.description", new Object[] { invoiceId }, client.getLangAsLocale());
        long price = items.stream().collect(Collectors.summingLong(Item::getPrice));
        Transaction transaction = new Transaction(client, invoiceId, new Date(), description, price);
        transactionDao.save(transaction);

        // Update the items
        for (Item item : items) {
            item.setInvoiceId(invoiceId);
        }
        itemDao.saveAll(items);

        return transaction;
    }

    private InputStream genPdf(Transaction transaction) {

        // Get the extra details
        Client client = transaction.getClient();
        List<Item> items = itemDao.findAllByInvoiceId(transaction.getInvoiceId());
        List<TransactionWithBalance> recentsTransactions = getRecentTransactions(client);
        long accountBalance = transactionDao.findTotalByClient(client);

        // Create the HTML
        Map<String, Object> model = new HashMap<>();
        model.put("client", client);
        model.put("currentTransaction", transaction);
        model.put("items", items);
        model.put("recentsTransactions", recentsTransactions);
        model.put("totalPrice", transaction.getPriceFormatted());
        model.put("accountBalance", accountBalance);
        model.put("accountBalanceFormatted", PriceFormatTools.toDigit(accountBalance));
        model.put("negativeAccountBalanceFormatted", PriceFormatTools.toDigit(-accountBalance));

        File tmpFolder = null;
        try {
            tmpFolder = java.nio.file.Files.createTempDirectory("invoice").toFile();
            String tmpFolderAbs = tmpFolder.getAbsolutePath();

            // Process template
            Template template = freemarkerConfiguration.getTemplate("invoice-" + client.getLang() + ".html");
            FileOutputStream htmlOutputStream = new FileOutputStream(tmpFolderAbs + "/index.html");
            template.process(model, new OutputStreamWriter(htmlOutputStream));
            htmlOutputStream.close();

            // Copy extra files
            if (Strings.isNullOrEmpty(emailTemplateDirectory)) {
                // Copy default
                ResourceTools.copyToFile("/com/foilen/crm/services/email/logo.png", new File(tmpFolderAbs + "/logo.png"));
            } else {
                // Copy all files from the directory
                DirectoryTools.listFilesAndFoldersRecursively(emailTemplateDirectory, false).forEach(fileOrDirName -> {
                    File fileOrDir = new File(emailTemplateDirectory + "/" + fileOrDirName);
                    String fileOrDirInTmp = tmpFolderAbs + "/" + fileOrDirName;
                    if (fileOrDir.isDirectory()) {
                        DirectoryTools.createPath(fileOrDirInTmp);
                    } else {
                        try {
                            logger.debug("Copy {} -> {}", fileOrDir, fileOrDirInTmp);
                            Files.copy(fileOrDir, new File(fileOrDirInTmp));
                        } catch (IOException e) {
                            logger.error("Problem copying files", e);
                            throw new CrmException("Problem copying files", e);
                        }
                    }
                });

            }

            // Render invoice
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(tmpFolder.toURI().toString() + "/index.html");
            renderer.layout();
            renderer.createPDF(pdfOutputStream);
            return new ByteArrayInputStream(pdfOutputStream.toByteArray());
        } catch (Exception e) {
            throw new CrmException("Problem generating the html invoice", e);
        } finally {
            if (tmpFolder != null) {
                DirectoryTools.deleteFolder(tmpFolder);
            }
        }

    }

    protected List<TransactionWithBalance> getRecentTransactions(Client client) {
        List<TransactionWithBalance> recentsTransactions = transactionDao.findFirst5ByClientOrderByDateDesc(client) //
                .stream() //
                .map(it -> JsonTools.clone(it, TransactionWithBalance.class)) //
                .sorted((a, b) -> a.getDate().compareTo(b.getDate())) //
                .collect(Collectors.toList());
        long accountBalance = transactionDao.findTotalByClient(client);
        long cumulativePrice = accountBalance;
        for (int i = recentsTransactions.size() - 1; i >= 0; --i) {
            TransactionWithBalance transactionWithBalance = recentsTransactions.get(i);
            transactionWithBalance.setBalanceFormatted(cumulativePrice);
            cumulativePrice -= transactionWithBalance.getPrice();
        }
        return recentsTransactions;
    }

    @Override
    public TransactionList listAll(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewTransactionOrFail(userId);

        // Retrieve
        TransactionList result = new TransactionList();
        Page<Transaction> page = transactionDao
                .findAll(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Sort.by(Order.desc("date"), Order.asc("client.name"), Order.desc("invoiceId"), Order.asc("id"))));
        paginationService.wrap(result, page, com.foilen.crm.web.model.Transaction.class);
        return result;
    }

    @Override
    public void sendInvoice(Transaction transaction) {

        // Send email
        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setFrom(mailFrom);
        emailBuilder.addTo(transaction.getClient().getEmail());
        emailBuilder.addCc(mailFrom);
        emailBuilder.addAttachmentFromStream(transaction.getInvoiceId() + ".pdf", genPdf(transaction));
        emailBuilder.setSubject(messageSource.getMessage("email.subject", new Object[] { company, transaction.getInvoiceId() }, transaction.getClient().getLangAsLocale()));
        emailBuilder.setBodyTextFromString(messageSource.getMessage("email.body", new Object[] {}, transaction.getClient().getLangAsLocale()));

        emailService.sendEmail(emailBuilder);

    }

}

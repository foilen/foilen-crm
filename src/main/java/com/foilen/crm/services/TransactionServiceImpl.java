package com.foilen.crm.services;

import com.foilen.crm.db.repository.ItemRepository;
import com.foilen.crm.db.repository.TransactionRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.exception.CrmException;
import com.foilen.crm.web.model.CreateOrUpdatePayment;
import com.foilen.crm.web.model.TransactionList;
import com.foilen.crm.web.model.ClientShort;
import com.foilen.crm.web.model.TransactionExtended;
import com.foilen.smalltools.email.EmailBuilder;
import com.foilen.smalltools.email.EmailService;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.*;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl extends AbstractApiService implements TransactionService {

    @Autowired
    private Configuration freemarkerConfiguration;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${crm.company}")
    private String company;
    @Value("${crm.emailTemplateDirectory:#{null}}")
    private String emailTemplateDirectory;
    @Value("${crm.mailFrom}")
    private String mailFrom;
    @Value("${crm.mailForceEmailTo:#{null}}")
    private String mailForceEmailTo;

    @Override
    public FormResult create(String userId, CreateOrUpdatePayment form) {

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
        String paymentMessage = messageSource.getMessage("transaction.create.paymentDescription", new Object[]{form.getPaymentType()}, client.getLangAsLocale());

        Transaction entity = JsonTools.clone(form, Transaction.class);
        entity.setClientId(client.getId());
        entity.setDescription(paymentMessage);
        entity.setPriceInCents(entity.getPriceInCents() * -1);
        transactionRepository.save(entity);

        return formResult;

    }

    @Override
    public Transaction createTransaction(Client client, List<Item> items, String invoicePrefix, AtomicLong nextInvoiceSuffix) {

        // Find the next available id
        String invoiceId = null;
        while (invoiceId == null) {
            invoiceId = invoicePrefix + "-" + nextInvoiceSuffix.getAndIncrement();
            logger.info("Checking if invoice id {} is available", invoiceId);
            if (transactionRepository.findByInvoiceId(invoiceId) != null) {
                invoiceId = null;
            }
        }

        logger.info("Using invoice id {}", invoiceId);

        // Create the transaction
        String description = messageSource.getMessage("transaction.create.description", new Object[]{invoiceId}, client.getLangAsLocale());
        long price = items.stream().collect(Collectors.summingLong(Item::getPriceInCents));
        Transaction transaction = new Transaction(client.getId(), invoiceId, new Date(), description, price);
        transactionRepository.save(transaction);

        // Update the items
        for (Item item : items) {
            item.setInvoiceId(invoiceId);
        }
        itemRepository.saveAll(items);

        return transaction;
    }

    private InputStream genPdf(Transaction transaction) {

        // Get the extra details
        Client client = clientRepository.findById(transaction.getClientId()).orElse(null);
        List<Item> items = itemRepository.findAllByInvoiceId(transaction.getInvoiceId());
        List<TransactionExtended> recentsTransactions = getRecentTransactions(client);
        long accountBalance = transactionRepository.findTotalByClientId(client.getId());

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
            File htmlFile = new File(tmpFolderAbs + "/index.html");
            HtmlConverter.convertToPdf(new FileInputStream(htmlFile), pdfOutputStream, new ConverterProperties().setBaseUri(tmpFolder.toURI().toString()));
            return new ByteArrayInputStream(pdfOutputStream.toByteArray());
        } catch (Exception e) {
            throw new CrmException("Problem generating the html invoice", e);
        } finally {
            if (tmpFolder != null) {
                DirectoryTools.deleteFolder(tmpFolder);
            }
        }

    }

    protected List<TransactionExtended> getRecentTransactions(Client client) {
        List<TransactionExtended> recentsTransactions = transactionRepository.findFirst5ByClientIdOrderByDateDesc(client.getId())
                .stream()
                .map(it -> JsonTools.clone(it, TransactionExtended.class))
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
        long accountBalance = transactionRepository.findTotalByClientId(client.getId());

        // Resolve the technicalSupportId reference once for the whole list
        com.foilen.crm.web.model.ClientExtended clientExtended = JsonTools.clone(client, com.foilen.crm.web.model.ClientExtended.class);
        if (client.getTechnicalSupportId() != null) {
            clientExtended.setTechnicalSupport(technicalSupportsByIds(Set.of(client.getTechnicalSupportId())).get(client.getTechnicalSupportId()));
        }

        long cumulativePrice = accountBalance;
        for (int i = recentsTransactions.size() - 1; i >= 0; --i) {
            TransactionExtended transactionExtended = recentsTransactions.get(i);
            transactionExtended.setClient(clientExtended);
            transactionExtended.setBalanceFormatted(cumulativePrice);
            cumulativePrice -= transactionExtended.getPriceInCents();
        }
        return recentsTransactions;
    }

    @Override
    public TransactionList listAll(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewTransactionOrFail(userId);
        var clientIdFilter = ownedClientIdsOrNullIfAdmin(userId);

        // Retrieve
        TransactionList result = new TransactionList();
        Page<Transaction> page = transactionRepository.findAllSortedByClientName(PageRequest.of(pageId - 1, paginationService.getItemsPerPage()), clientIdFilter);
        paginationService.wrap(result, page, com.foilen.crm.web.model.Transaction.class);

        // Resolve the clientId reference on each item
        Map<String, com.foilen.crm.web.model.ClientShort> clientShorts = clientShortsByIds(page.getContent().stream()
                .map(Transaction::getClientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        List<com.foilen.crm.web.model.Transaction> items = result.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setClient(clientShorts.get(page.getContent().get(i).getClientId()));
        }

        return result;
    }

    @Override
    public void sendInvoice(Transaction transaction) {

        Client client = clientRepository.findById(transaction.getClientId()).orElse(null);
        String to = client.getEmail();
        String subject = messageSource.getMessage("email.subject", new Object[]{company, transaction.getInvoiceId()}, client.getLangAsLocale());

        if (mailForceEmailTo != null) {
            subject = "[FORCED] " + to + " | " + subject;
            to = mailForceEmailTo;
            logger.warn("Forcing email to {}", to);
        }

        // Send email
        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setFrom(mailFrom);
        emailBuilder.addTo(to);
        emailBuilder.addCc(mailFrom);
        emailBuilder.addAttachmentFromStream(transaction.getInvoiceId() + ".pdf", genPdf(transaction));
        emailBuilder.setSubject(subject);
        emailBuilder.setBodyTextFromString(messageSource.getMessage("email.body", new Object[]{}, client.getLangAsLocale()));

        emailService.sendEmail(emailBuilder);

    }

    @Override
    public FormResult update(String userId, String id, CreateOrUpdatePayment form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canUpdatePaymentOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "paymentType", form.getPaymentType());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Find the transaction
        Transaction entity = transactionRepository.findById(id).orElse(null);
        if (entity == null) {
            formResult.getGlobalErrors().add("error.notFound");
            return formResult;
        }

        // Validate that the transaction doesn't have an invoiceId
        if (entity.getInvoiceId() != null) {
            formResult.getGlobalErrors().add("error.cannotUpdateInvoicedTransaction");
            return formResult;
        }

        // Update
        String paymentMessage = messageSource.getMessage("transaction.create.paymentDescription", new Object[]{form.getPaymentType()}, client.getLangAsLocale());

        entity.setClientId(client.getId());
        entity.setDescription(paymentMessage);
        entity.setDate(DateTools.parseDateOnly(form.getDate()));
        entity.setPriceInCents(form.getPriceInCents() * -1);
        transactionRepository.save(entity);

        return formResult;

    }

}

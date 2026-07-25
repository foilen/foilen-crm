package com.foilen.crm.services;

import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.ExportClient;
import com.foilen.crm.web.model.ExportModel;
import com.foilen.crm.web.model.AdminExportResult;
import com.foilen.crm.web.model.ExportItem;
import com.foilen.crm.web.model.ExportRecurrentItem;
import com.foilen.crm.web.model.ExportTechnicalSupport;
import com.foilen.crm.web.model.ExportTransaction;
import com.foilen.crm.web.model.ExportUser;
import com.foilen.smalltools.restapi.model.FormResult;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl extends AbstractApiService implements AdminService {

    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private UserDao userDao;

    @Override
    public AdminExportResult exportAll(String userId) {

        // Validation
        entitlementService.canExportDataOrFail(userId);

        // Retrieve
        ExportModel exportModel = new ExportModel();

        exportModel.setTechnicalSupports(technicalSupportDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setClients(clientDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setItems(itemDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setRecurrentItems(recurrentItemDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setTransactions(transactionDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setUsers(userDao.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));

        AdminExportResult result = new AdminExportResult();
        result.setItem(exportModel);
        return result;
    }

    @Override
    public FormResult importAll(String userId, ExportModel exportModel) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canImportDataOrFail(userId);

        if (exportModel == null) {
            formResult.getGlobalErrors().add("error.mandatory");
            return formResult;
        }

        // Wipe all the data. Children first to respect the foreign keys
        itemDao.deleteAllInBatch();
        recurrentItemDao.deleteAllInBatch();
        transactionDao.deleteAllInBatch();
        clientDao.deleteAllInBatch();
        technicalSupportDao.deleteAllInBatch();
        userDao.deleteAllInBatch();

        // Technical Supports
        Map<String, TechnicalSupport> technicalSupportBySid = new HashMap<>();
        for (ExportTechnicalSupport item : exportModel.getTechnicalSupports()) {
            TechnicalSupport entity = new TechnicalSupport(item.getSid(), item.getPricePerHour());
            technicalSupportDao.save(entity);
            technicalSupportBySid.put(entity.getSid(), entity);
        }

        // Clients
        Map<String, Client> clientByShortName = new HashMap<>();
        for (ExportClient item : exportModel.getClients()) {
            Client entity = new Client()
                    .setName(item.getName())
                    .setShortName(item.getShortName())
                    .setContactName(item.getContactName())
                    .setEmail(item.getEmail())
                    .setAddress(item.getAddress())
                    .setTel(item.getTel())
                    .setMainSite(item.getMainSite())
                    .setLang(item.getLang())
                    .setTechnicalSupport(technicalSupportBySid.get(item.getTechnicalSupportSid()));
            clientDao.save(entity);
            clientByShortName.put(entity.getShortName(), entity);
        }

        // Items
        for (ExportItem item : exportModel.getItems()) {
            Item entity = new Item(
                    clientByShortName.get(item.getClientShortName()),
                    item.getInvoiceId(),
                    item.getDate(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getCategory());
            itemDao.save(entity);
        }

        // Recurrent Items
        for (ExportRecurrentItem item : exportModel.getRecurrentItems()) {
            RecurrentItem entity = new RecurrentItem(
                    clientByShortName.get(item.getClientShortName()),
                    item.getDescription(),
                    item.getPrice(),
                    item.getCategory(),
                    item.getCalendarUnit(),
                    item.getDelta(),
                    item.getNextGenerationDate());
            recurrentItemDao.save(entity);
        }

        // Transactions
        for (ExportTransaction item : exportModel.getTransactions()) {
            Transaction entity = new Transaction(
                    clientByShortName.get(item.getClientShortName()),
                    item.getInvoiceId(),
                    item.getDate(),
                    item.getDescription(),
                    item.getPrice());
            transactionDao.save(entity);
        }

        // Users
        for (ExportUser item : exportModel.getUsers()) {
            User entity = new User(item.getUserId(), item.isAdmin());
            entity.setEmail(item.getEmail());
            userDao.save(entity);
        }

        return formResult;
    }

    private ExportClient toExport(Client entity) {
        ExportClient item = new ExportClient();
        item.setId(entity.getId());
        item.setName(entity.getName());
        item.setShortName(entity.getShortName());
        item.setContactName(entity.getContactName());
        item.setEmail(entity.getEmail());
        item.setAddress(entity.getAddress());
        item.setTel(entity.getTel());
        item.setMainSite(entity.getMainSite());
        item.setLang(entity.getLang());
        item.setTechnicalSupportSid(entity.getTechnicalSupport() == null ? null : entity.getTechnicalSupport().getSid());
        return item;
    }

    private ExportItem toExport(Item entity) {
        ExportItem item = new ExportItem();
        item.setId(entity.getId());
        item.setClientShortName(entity.getClient() == null ? null : entity.getClient().getShortName());
        item.setInvoiceId(entity.getInvoiceId());
        item.setDate(entity.getDate());
        item.setDescription(entity.getDescription());
        item.setPrice(entity.getPrice());
        item.setCategory(entity.getCategory());
        return item;
    }

    private ExportRecurrentItem toExport(RecurrentItem entity) {
        ExportRecurrentItem item = new ExportRecurrentItem();
        item.setId(entity.getId());
        item.setCalendarUnit(entity.getCalendarUnit());
        item.setDelta(entity.getDelta());
        item.setNextGenerationDate(entity.getNextGenerationDate());
        item.setClientShortName(entity.getClient() == null ? null : entity.getClient().getShortName());
        item.setDescription(entity.getDescription());
        item.setPrice(entity.getPrice());
        item.setCategory(entity.getCategory());
        return item;
    }

    private ExportTechnicalSupport toExport(TechnicalSupport entity) {
        ExportTechnicalSupport item = new ExportTechnicalSupport();
        item.setId(entity.getId());
        item.setSid(entity.getSid());
        item.setPricePerHour(entity.getPricePerHour());
        return item;
    }

    private ExportTransaction toExport(Transaction entity) {
        ExportTransaction item = new ExportTransaction();
        item.setId(entity.getId());
        item.setClientShortName(entity.getClient() == null ? null : entity.getClient().getShortName());
        item.setInvoiceId(entity.getInvoiceId());
        item.setDate(entity.getDate());
        item.setDescription(entity.getDescription());
        item.setPrice(entity.getPrice());
        return item;
    }

    private ExportUser toExport(User entity) {
        ExportUser item = new ExportUser();
        item.setId(entity.getId());
        item.setUserId(entity.getUserId());
        item.setAdmin(entity.isAdmin());
        item.setEmail(entity.getEmail());
        return item;
    }

}

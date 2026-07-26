package com.foilen.crm.services;

import com.foilen.crm.db.repository.TransactionRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl extends AbstractApiService implements AdminService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public AdminExportResult exportAll(String userId) {

        // Validation
        entitlementService.canExportDataOrFail(userId);

        // Retrieve
        ExportModel exportModel = new ExportModel();

        exportModel.setTechnicalSupports(technicalSupportRepository.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setClients(clientRepository.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setItems(itemRepository.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setRecurrentItems(recurrentItemRepository.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setTransactions(transactionRepository.findAll().stream()
                .map(this::toExport)
                .collect(Collectors.toList()));
        exportModel.setUsers(userRepository.findAll().stream()
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

        // Wipe all the data. Children first for consistency (no DB-level foreign keys with MongoDB)
        itemRepository.deleteAll();
        recurrentItemRepository.deleteAll();
        transactionRepository.deleteAll();
        clientRepository.deleteAll();
        technicalSupportRepository.deleteAll();
        userRepository.deleteAll();

        // Technical Supports
        Map<String, TechnicalSupport> technicalSupportBySid = new HashMap<>();
        for (ExportTechnicalSupport item : exportModel.getTechnicalSupports()) {
            TechnicalSupport entity = new TechnicalSupport(item.getSid(), item.getPricePerHourInCents());
            technicalSupportRepository.save(entity);
            technicalSupportBySid.put(entity.getSid(), entity);
        }

        // Clients
        Map<String, Client> clientByShortName = new HashMap<>();
        for (ExportClient item : exportModel.getClients()) {
            TechnicalSupport technicalSupport = technicalSupportBySid.get(item.getTechnicalSupportSid());
            Client entity = new Client()
                    .setName(item.getName())
                    .setShortName(item.getShortName())
                    .setContactName(item.getContactName())
                    .setEmail(item.getEmail())
                    .setAddress(item.getAddress())
                    .setTel(item.getTel())
                    .setMainSite(item.getMainSite())
                    .setLang(item.getLang())
                    .setTechnicalSupportId(technicalSupport == null ? null : technicalSupport.getId());
            clientRepository.save(entity);
            clientByShortName.put(entity.getShortName(), entity);
        }

        // Items
        for (ExportItem item : exportModel.getItems()) {
            Client client = clientByShortName.get(item.getClientShortName());
            Item entity = new Item(
                    client == null ? null : client.getId(),
                    item.getInvoiceId(),
                    item.getDate(),
                    item.getDescription(),
                    item.getPriceInCents(),
                    item.getCategory());
            itemRepository.save(entity);
        }

        // Recurrent Items
        for (ExportRecurrentItem item : exportModel.getRecurrentItems()) {
            Client client = clientByShortName.get(item.getClientShortName());
            RecurrentItem entity = new RecurrentItem(
                    client == null ? null : client.getId(),
                    item.getDescription(),
                    item.getPriceInCents(),
                    item.getCategory(),
                    item.getCalendarUnit(),
                    item.getDelta(),
                    item.getNextGenerationDate());
            recurrentItemRepository.save(entity);
        }

        // Transactions
        for (ExportTransaction item : exportModel.getTransactions()) {
            Client client = clientByShortName.get(item.getClientShortName());
            Transaction entity = new Transaction(
                    client == null ? null : client.getId(),
                    item.getInvoiceId(),
                    item.getDate(),
                    item.getDescription(),
                    item.getPriceInCents());
            transactionRepository.save(entity);
        }

        // Users
        for (ExportUser item : exportModel.getUsers()) {
            User entity = new User(item.getEmail(), item.isAdmin());
            entity.setDisabled(item.isDisabled());
            userRepository.save(entity);
        }

        return formResult;
    }

    private ExportClient toExport(Client entity) {
        TechnicalSupport technicalSupport = entity.getTechnicalSupportId() == null ? null : technicalSupportRepository.findById(entity.getTechnicalSupportId()).orElse(null);
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
        item.setTechnicalSupportSid(technicalSupport == null ? null : technicalSupport.getSid());
        return item;
    }

    private ExportItem toExport(Item entity) {
        Client client = entity.getClientId() == null ? null : clientRepository.findById(entity.getClientId()).orElse(null);
        ExportItem item = new ExportItem();
        item.setId(entity.getId());
        item.setClientShortName(client == null ? null : client.getShortName());
        item.setInvoiceId(entity.getInvoiceId());
        item.setDate(entity.getDate());
        item.setDescription(entity.getDescription());
        item.setPriceInCents(entity.getPriceInCents());
        item.setCategory(entity.getCategory());
        return item;
    }

    private ExportRecurrentItem toExport(RecurrentItem entity) {
        Client client = entity.getClientId() == null ? null : clientRepository.findById(entity.getClientId()).orElse(null);
        ExportRecurrentItem item = new ExportRecurrentItem();
        item.setId(entity.getId());
        item.setCalendarUnit(entity.getCalendarUnit());
        item.setDelta(entity.getDelta());
        item.setNextGenerationDate(entity.getNextGenerationDate());
        item.setClientShortName(client == null ? null : client.getShortName());
        item.setDescription(entity.getDescription());
        item.setPriceInCents(entity.getPriceInCents());
        item.setCategory(entity.getCategory());
        return item;
    }

    private ExportTechnicalSupport toExport(TechnicalSupport entity) {
        ExportTechnicalSupport item = new ExportTechnicalSupport();
        item.setId(entity.getId());
        item.setSid(entity.getSid());
        item.setPricePerHourInCents(entity.getPricePerHourInCents());
        return item;
    }

    private ExportTransaction toExport(Transaction entity) {
        Client client = entity.getClientId() == null ? null : clientRepository.findById(entity.getClientId()).orElse(null);
        ExportTransaction item = new ExportTransaction();
        item.setId(entity.getId());
        item.setClientShortName(client == null ? null : client.getShortName());
        item.setInvoiceId(entity.getInvoiceId());
        item.setDate(entity.getDate());
        item.setDescription(entity.getDescription());
        item.setPriceInCents(entity.getPriceInCents());
        return item;
    }

    private ExportUser toExport(User entity) {
        ExportUser item = new ExportUser();
        item.setEmail(entity.getEmail());
        item.setAdmin(entity.isAdmin());
        item.setDisabled(entity.isDisabled());
        return item;
    }

}

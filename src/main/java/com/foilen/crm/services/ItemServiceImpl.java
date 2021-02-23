/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.foilen.crm.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.CollectionsTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.TimeConverterTools;

@Service
@Transactional
public class ItemServiceImpl extends AbstractApiService implements ItemService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private TransactionService transactionService;

    @Override
    public FormResult billPending(String userId, String invoicePrefix) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canBillItemOrFail(userId);
        validateMandatory(formResult, "invoicePrefix", invoicePrefix);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Per client
        AtomicLong invoiceSuffix = new AtomicLong(1);
        List<Client> clients = itemDao.findAllClientByInvoiceIdNull();
        List<Transaction> newTransactions = new ArrayList<>();
        for (Client client : clients) {
            logger.info("Processing client {}", client);

            // Create a transaction
            List<Item> items = itemDao.findAllByInvoiceIdIsNullAndClient(client);
            logger.info("Client {} has {} pending items", client, items.size());
            if (items.isEmpty()) {
                continue;
            }

            Transaction transaction = transactionService.createTransaction(client, items, invoicePrefix, invoiceSuffix);
            newTransactions.add(transaction);
        }

        // Send emails
        newTransactions.forEach(transaction -> transactionService.sendInvoice(transaction));

        return formResult;
    }

    @Override
    public FormResult billSomePending(String userId, BillSomePendingItems form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canBillItemOrFail(userId);
        validateMandatory(formResult, "invoicePrefix", form.getInvoicePrefix());

        // Get all items
        List<Item> itemsToBill = itemDao.findAllById(form.getItemToBillIds());

        // Ensure all found
        if (itemsToBill.size() != form.getItemToBillIds().size()) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "itemToBillIds", String.class).add("error.mandatory");
        }

        // Ensure all not billed
        if (itemsToBill.stream().anyMatch(it -> it.getInvoiceId() != null)) {
            CollectionsTools.getOrCreateEmptyArrayList(formResult.getValidationErrorsByField(), "itemToBillIds", String.class).add("error.someAlreadyBilled");
        }

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Put per client
        Map<Client, List<Item>> itemsPerClient = itemsToBill.stream().collect(Collectors.groupingBy(Item::getClient));

        // Per client
        AtomicLong invoiceSuffix = new AtomicLong(1);
        List<Transaction> newTransactions = new ArrayList<>();
        for (Client client : itemsPerClient.keySet()) {
            logger.info("Processing client {}", client);

            // Create a transaction
            List<Item> items = itemsPerClient.get(client);
            logger.info("Client {} has {} pending items", client, items.size());
            if (items.isEmpty()) {
                continue;
            }

            Transaction transaction = transactionService.createTransaction(client, items, form.getInvoicePrefix(), invoiceSuffix);
            newTransactions.add(transaction);
        }

        // Send emails
        newTransactions.forEach(transaction -> transactionService.sendInvoice(transaction));

        return formResult;
    }

    @Override
    public FormResult create(String userId, CreateItem form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        Item entity = JsonTools.clone(form, Item.class);
        entity.setClient(client);
        itemDao.save(entity);

        return formResult;

    }

    @Override
    public FormResult create(String userId, CreateItemWithTime form) {

        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());
        TechnicalSupport technicalSupport = validateTechnicalSupportByClient(formResult, "clientShortName", client);

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // Create
        Item entity = JsonTools.clone(form, Item.class);
        entity.setClient(client);

        // Calculate time and price
        long minutes = form.getHours() * 60 + form.getMinutes();
        double price = minutes / 60d * technicalSupport.getPricePerHour();
        entity.setPrice(Math.round(price));

        entity.setDescription(entity.getDescription() + " (" + TimeConverterTools.convertToTextFromMin(minutes) + ")");

        itemDao.save(entity);

        return formResult;

    }

    @Override
    public FormResult update(String userId, UpdateItem form) {
        FormResult formResult = new FormResult();

        // Validation
        entitlementService.canCreateItemOrFail(userId);
        validateMandatory(formResult, "clientShortName", form.getClientShortName());
        validateDateOnly(formResult, "date", form.getDate());
        validateMandatory(formResult, "date", form.getDate());
        validateMandatory(formResult, "description", form.getDescription());
        validateMandatory(formResult, "category", form.getCategory());
        Client client = validateClientByShortName(formResult, "clientShortName", form.getClientShortName());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        Item entity = JsonTools.clone(form, Item.class);
        entity.setClient(client);

        itemDao.save(entity);

        return formResult;
    }

    @Override
    public FormResult delete(long id) {
        FormResult formResult = new FormResult();

        itemDao.deleteById(id);

        return formResult;
    }

    @Override
    public ItemList listBilled(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewItemAllOrFail(userId);

        // Retrieve
        ItemList result = new ItemList();
        Page<Item> page = itemDao.findAllByInvoiceIdNotNull(
                PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Sort.by(Order.desc("invoiceId"), Order.asc("client.name"), Order.desc("date"), Order.asc("id"))));
        paginationService.wrap(result, page, com.foilen.crm.web.model.Item.class);
        return result;
    }

    @Override
    public ItemList listPending(String userId, int pageId) {

        // Validation
        validatePageId(pageId);
        entitlementService.canViewItemAllOrFail(userId);

        // Retrieve
        ItemList result = new ItemList();
        Page<Item> page = itemDao.findAllByInvoiceIdNull(PageRequest.of(pageId - 1, paginationService.getItemsPerPage(), Sort.by(Order.asc("client.name"), Order.desc("date"), Order.asc("id"))));
        paginationService.wrap(result, page, com.foilen.crm.web.model.Item.class);
        return result;
    }

}

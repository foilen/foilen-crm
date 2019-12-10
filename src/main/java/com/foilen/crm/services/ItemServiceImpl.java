/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.CreateItem;
import com.foilen.crm.web.model.ItemList;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.tools.JsonTools;

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

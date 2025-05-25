package com.foilen.crm.services;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.CreateOrUpdatePayment;
import com.foilen.crm.web.model.TransactionList;
import com.foilen.smalltools.restapi.model.FormResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public interface TransactionService {

    FormResult create(String userId, CreateOrUpdatePayment form);

    /**
     * Create a transaction.
     *
     * @param client            the client that will be billed
     * @param items             the items to add to the transaction
     * @param invoicePrefix     the prefix of the invoice
     * @param nextInvoiceSuffix the next invoice suffix to use. Will check if available and if already taken, will try the next one
     * @return the created transaction
     */
    Transaction createTransaction(Client client, List<Item> items, String invoicePrefix, AtomicLong nextInvoiceSuffix);

    /**
     * Get the list of transactions.
     *
     * @param userId the user that wants the list
     * @param pageId the page id starting at 1
     * @return the list of transactions
     */
    TransactionList listAll(String userId, int pageId);

    void sendInvoice(Transaction transaction);

    FormResult update(String userId, long id, CreateOrUpdatePayment form);

}

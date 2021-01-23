/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.smalltools.test.asserts.AssertTools;

public class ItemServiceImplTest extends AbstractSpringTests {

    @Autowired
    private ItemService itemService;

    public ItemServiceImplTest() {
        super(true);
    }

    @Test
    public void testBillPending_alreadyUsedPrefix() {
        itemService.billPending(FakeDataServiceImpl.USER_ID_ADMIN, "I190601");

        AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_alreadyUsedPrefix-transactions.json", getClass(),
                trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
        AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_alreadyUsedPrefix-items.json", getClass(), trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

    }

    @Test
    public void testBillPending_neverUsedPrefix() {

        List<?> initialTransactions = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));
        List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

        itemService.billPending(FakeDataServiceImpl.USER_ID_ADMIN, "I190701");

        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillPending_neverUsedPrefix-transactions.json", getClass(), initialTransactions,
                trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillPending_neverUsedPrefix-items.json", getClass(), initialItems,
                trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
    }

    @Test
    public void testBillSomePending_neverUsedPrefix() {

        List<?> initialTransactions = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));
        List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

        BillSomePendingItems form = new BillSomePendingItems();
        form.setInvoicePrefix("I190701");

        form.getItemToBillIds().add(itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("avez"), "Shared hosting - L1").getId());
        form.getItemToBillIds().add(itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("zooa"), "Shared hosting - L2").getId());

        itemService.billSomePending(FakeDataServiceImpl.USER_ID_ADMIN, form);

        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillSomePending_neverUsedPrefix-transactions.json", getClass(), initialTransactions,
                trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillSomePending_neverUsedPrefix-items.json", getClass(), initialItems,
                trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

    }

}

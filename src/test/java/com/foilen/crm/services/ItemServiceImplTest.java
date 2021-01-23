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
import com.foilen.crm.web.model.CreateItemWithTime;
import com.foilen.smalltools.restapi.model.FormResult;
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

    @Test
    public void testCreateWithTime() {

        List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

        CreateItemWithTime form = new CreateItemWithTime();
        form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
        form.setCategory("technical support");
        form.setDate("2019-08-08");
        form.setDescription("Fixing a code 18");
        form.setHours(2);
        form.setMinutes(10);

        FormResult result = itemService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testCreateWithTime-items.json", getClass(), initialItems, trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

    }

    @Test
    public void testCreateWithTime_noTechSupport_FAIL() {

        CreateItemWithTime form = new CreateItemWithTime();
        form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_EXTRA);
        form.setCategory("technical support");
        form.setDate("2019-08-08");
        form.setDescription("Fixing a code 18");
        form.setHours(2);
        form.setMinutes(10);

        FormResult result = itemService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testCreateWithTime_noTechSupport_FAIL.json", getClass(), result);

    }

    @Test
    public void testCreateWithTime_rolling() {

        List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

        CreateItemWithTime form = new CreateItemWithTime();
        form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
        form.setCategory("technical support");
        form.setDate("2019-08-08");
        form.setDescription("Fixing a code 18");
        form.setMinutes(130);

        FormResult result = itemService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testCreateWithTime-items.json", getClass(), initialItems, trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

    }

}

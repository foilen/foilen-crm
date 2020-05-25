/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
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
        itemService.billPending(FakeDataServiceImpl.USER_ID_ADMIN, "I190701");

        AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_neverUsedPrefix-transactions.json", getClass(),
                trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
        AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_neverUsedPrefix-items.json", getClass(), trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

    }

}

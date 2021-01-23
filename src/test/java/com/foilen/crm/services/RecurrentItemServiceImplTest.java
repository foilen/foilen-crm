/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.smalltools.test.asserts.AssertTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;

public class RecurrentItemServiceImplTest extends AbstractSpringTests {

    @Autowired
    private RecurrentItemService recurrentItemService;

    public RecurrentItemServiceImplTest() {
        super(true);
    }

    @Test
    public void testGenerateReady_none() {

        String initialItems = JsonTools.prettyPrintWithoutNulls(itemDao.findAll(Sort.by("invoiceId", "description")));
        String initialRecurrentItems = JsonTools.prettyPrintWithoutNulls(recurrentItemDao.findAll(Sort.by("client.name", "description")));

        recurrentItemService.generateReady(DateTools.parseFull("2019-06-30 23:59:00"));

        String finalItems = JsonTools.prettyPrintWithoutNulls(itemDao.findAll(Sort.by("invoiceId", "description")));
        String finalRecurrentItems = JsonTools.prettyPrintWithoutNulls(recurrentItemDao.findAll(Sort.by("client.name", "description")));
        Assert.assertEquals(initialItems, finalItems);
        Assert.assertEquals(initialRecurrentItems, finalRecurrentItems);

    }

    @Test
    public void testGenerateReady_ok() {

        recurrentItemService.generateReady(DateTools.parseFull("2019-07-01 00:45:00"));

        AssertTools.assertJsonComparisonWithoutNulls("RecurrentItemServiceImplTest-testGenerateReady_ok-items.json", getClass(), trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        AssertTools.assertJsonComparisonWithoutNulls("RecurrentItemServiceImplTest-testGenerateReady_ok-recurrentItems.json", getClass(),
                trimRecurrentItem(recurrentItemDao.findAll(Sort.by("client.name", "description"))));

    }

}

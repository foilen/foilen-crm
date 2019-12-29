/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.CreatePayment;
import com.foilen.crm.web.model.Transaction;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertTools;

public class TransactionServiceImplTest extends AbstractSpringTests {

    @Autowired
    private TransactionService transactionService;

    public TransactionServiceImplTest() {
        super(true);
    }

    @Test
    public void testCreate() {

        // Initial
        List<Transaction> initialItems = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));

        // Create
        CreatePayment form = new CreatePayment() //
                .setClientShortName("zooa") //
                .setDate("2019-06-25") //
                .setPaymentType("Paypal") //
                .setPrice(1000);
        FormResult formResult = transactionService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        Assert.assertTrue(formResult.isSuccess());

        // Final
        List<Transaction> finalItems = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));
        AssertTools.assertDiffJsonComparisonWithoutNulls("TransactionServiceImplTest-testCreate-transactions.json", getClass(), initialItems, finalItems);

    }

}

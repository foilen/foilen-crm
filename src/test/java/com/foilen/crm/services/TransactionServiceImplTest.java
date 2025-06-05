package com.foilen.crm.services;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.CreateOrUpdatePayment;
import com.foilen.crm.web.model.Transaction;
import com.foilen.crm.web.model.TransactionWithBalance;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertTools;
import com.foilen.smalltools.tools.DateTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@DisplayName("Transaction Service Implementation Tests")
public class TransactionServiceImplTest extends AbstractSpringTests {

    @Autowired
    private TransactionServiceImpl transactionService;

    public TransactionServiceImplTest() {
        super(true);
    }

    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {

        @Test
        @DisplayName("Admin users can create transactions")
        void createTransactionSucceeds() {
            // Initial
            List<Transaction> initialItems = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));

            // Create
            CreateOrUpdatePayment form = new CreateOrUpdatePayment()
                    .setClientShortName("zooa")
                    .setDate("2019-06-25")
                    .setPaymentType("Paypal")
                    .setPrice(1000);
            FormResult formResult = transactionService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
            Assertions.assertTrue(formResult.isSuccess());

            // Final
            List<Transaction> finalItems = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));
            AssertTools.assertDiffJsonComparisonWithoutNulls("TransactionServiceImplTest-testCreate-transactions.json", getClass(), 
                    initialItems, finalItems);
        }

        @Test
        @DisplayName("Can retrieve recent transactions for a client")
        void getRecentTransactionsSucceeds() {
            // Create transactions
            transactionDao.deleteAll();
            Client clientAvez = clientDao.findByShortName("avez");

            for (int i = 1; i <= 20; ++i) {
                com.foilen.crm.db.entities.invoice.Transaction transaction = new com.foilen.crm.db.entities.invoice.Transaction();
                transaction.setClient(clientAvez);
                transaction.setDate(DateTools.parseDateOnly("2019-01-" + i));
                transaction.setDescription("TX " + i);
                transaction.setInvoiceId("I" + i);
                transaction.setPrice(i * 100);
                transactionDao.save(transaction);
            }

            // Test
            List<TransactionWithBalance> recents = transactionService.getRecentTransactions(clientAvez);
            recents.forEach(it -> it.setId(null));
            recents.forEach(it -> it.getClient().setId(null));
            recents.forEach(it -> it.getClient().getTechnicalSupport().setId(null));

            // Assert
            AssertTools.assertJsonComparisonWithoutNulls("TransactionServiceImplTest-testGetRecentTransactions.json", getClass(), recents);
        }
    }

}

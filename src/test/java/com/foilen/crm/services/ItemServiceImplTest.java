package com.foilen.crm.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.crm.web.model.CreateItemWithTime;
import com.foilen.crm.web.model.CreateOrUpdateItem;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;
import com.foilen.smalltools.tools.DateTools;

@DisplayName("Item Service Implementation Tests")
public class ItemServiceImplTest extends AbstractSpringTests {

    @Autowired
    private ItemService itemService;

    public ItemServiceImplTest() {
        super(true);
    }

    @Nested
    @DisplayName("Bill Pending Items Tests")
    class BillPendingTests {

        @Test
        @DisplayName("Bill pending items with already used prefix")
        void billPendingWithAlreadyUsedPrefix() {
            itemService.billPending(FakeDataServiceImpl.USER_ID_ADMIN, "I190601");

            AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_alreadyUsedPrefix-transactions.json", getClass(),
                    trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
            AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testBillPending_alreadyUsedPrefix-items.json", getClass(), 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Bill pending items with never used prefix")
        void billPendingWithNeverUsedPrefix() {
            List<?> initialTransactions = trimTransaction(transactionDao.findAll(Sort.by("invoiceId")));
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            itemService.billPending(FakeDataServiceImpl.USER_ID_ADMIN, "I190701");

            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillPending_neverUsedPrefix-transactions.json", getClass(), initialTransactions,
                    trimTransaction(transactionDao.findAll(Sort.by("invoiceId"))));
            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testBillPending_neverUsedPrefix-items.json", getClass(), initialItems,
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Bill some pending items with never used prefix")
        void billSomePendingWithNeverUsedPrefix() {
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

    @Nested
    @DisplayName("Create Item Tests")
    class CreateItemTests {

        @Test
        @DisplayName("Create item with time specification")
        void createWithTime() {
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

            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testCreateWithTime-items.json", getClass(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Create item with time fails when no tech support")
        void createWithTimeNoTechSupportFails() {
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
        @DisplayName("Create item with rolling time calculation")
        void createWithTimeRolling() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            CreateItemWithTime form = new CreateItemWithTime();
            form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
            form.setCategory("technical support");
            form.setDate("2019-08-08");
            form.setDescription("Fixing a code 18");
            form.setMinutes(130);

            FormResult result = itemService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testCreateWithTime-items.json", getClass(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }
    }

    @Nested
    @DisplayName("Delete Item Tests")
    class DeleteItemTests {

        @Test
        @DisplayName("Non-admin users cannot delete items")
        void deleteNotAdminFails() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("avez"), "Shared hosting - L1").getId();

            expectNotAdmin(() ->
                itemService.delete(FakeDataServiceImpl.USER_ID_USER, itemId));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Cannot delete non-pending items")
        void deleteNotPendingFails() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findAllByInvoiceIdNotNull(PageRequest.of(0, 1)).getContent().getFirst().getId();

            FormResult result = itemService.delete(FakeDataServiceImpl.USER_ID_ADMIN, itemId);
            AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testDelete_notPending_FAIL.json", getClass(), result);

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Admin users can delete pending items")
        void deleteSucceeds() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("avez"), "Shared hosting - L1").getId();

            FormResult result = itemService.delete(FakeDataServiceImpl.USER_ID_ADMIN, itemId);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testDelete_OK-items.json", getClass(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }
    }

    @Nested
    @DisplayName("Update Item Tests")
    class UpdateItemTests {

        @Test
        @DisplayName("Non-admin users cannot update items")
        void updateNotAdminFails() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("avez"), "Shared hosting - L1").getId();

            CreateOrUpdateItem form = new CreateOrUpdateItem();
            form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
            form.setDate("2020-01-01");
            form.setDescription("New description");
            form.setPrice(1234);
            form.setCategory("new category");

            expectNotAdmin(() ->
                itemService.update(FakeDataServiceImpl.USER_ID_USER, itemId, form));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Cannot update non-pending items")
        void updateNotPendingFails() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findAllByInvoiceIdNotNull(PageRequest.of(0, 1)).getContent().getFirst().getId();

            CreateOrUpdateItem form = new CreateOrUpdateItem();
            form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
            form.setDate("2020-01-01");
            form.setDescription("New description");
            form.setPrice(1234);
            form.setCategory("new category");

            FormResult result = itemService.update(FakeDataServiceImpl.USER_ID_ADMIN, itemId, form);
            AssertTools.assertJsonComparisonWithoutNulls("ItemServiceImplTest-testUpdate_notPending_FAIL.json", getClass(), result);

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
        }

        @Test
        @DisplayName("Admin users can update pending items")
        void updateSucceeds() {
            List<?> initialItems = trimItem(itemDao.findAll(Sort.by("invoiceId", "description")));

            long itemId = itemDao.findByClientAndInvoiceIdNullAndDescription(clientDao.findByShortName("avez"), "Shared hosting - L1").getId();

            CreateOrUpdateItem form = new CreateOrUpdateItem();
            form.setClientShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
            form.setDate("2020-01-01");
            form.setDescription("New description");
            form.setPrice(1234);
            form.setCategory("new category");

            FormResult result = itemService.update(FakeDataServiceImpl.USER_ID_ADMIN, itemId, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("ItemServiceImplTest-testUpdate_OK-items.json", getClass(), initialItems, 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));

            Item item = itemDao.findById(itemId).get();
            Assertions.assertEquals("2020-01-01", DateTools.formatDateOnly(item.getDate()));
        }
    }

}

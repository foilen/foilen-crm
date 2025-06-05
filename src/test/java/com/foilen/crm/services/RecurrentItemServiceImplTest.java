package com.foilen.crm.services;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.CreateOrUpdateRecurrentItemForm;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Calendar;
import java.util.List;

@DisplayName("Recurrent Item Service Implementation Tests")
public class RecurrentItemServiceImplTest extends AbstractSpringTests {

    @Autowired
    private RecurrentItemService recurrentItemService;

    public RecurrentItemServiceImplTest() {
        super(true);
    }

    @Nested
    @DisplayName("Create Recurrent Item Tests")
    class CreateRecurrentItemTests {

        @Test
        @DisplayName("Non-admin users cannot create recurrent items")
        void createNotAdminFails() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                    .setCalendarUnit(Calendar.MONTH)
                    .setDelta(1)
                    .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                    .setDescription("Hosting L1")
                    .setPrice(500)
                    .setCategory("hosting");

            expectNotAdmin(() ->
                recurrentItemService.create(FakeDataServiceImpl.USER_ID_USER, form));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, 
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }

        @Test
        @DisplayName("Admin users can create recurrent items")
        void createSucceeds() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                    .setCalendarUnit(Calendar.MONTH)
                    .setDelta(1)
                    .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                    .setDescription("Hosting L1")
                    .setPrice(500)
                    .setCategory("hosting");

            FormResult result = recurrentItemService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("RecurrentItemServiceImplTest-testCreate_OK-recurrentItems.json", getClass(), initialRecurrentItems,
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }
    }

    @Nested
    @DisplayName("Delete Recurrent Item Tests")
    class DeleteRecurrentItemTests {

        @Test
        @DisplayName("Non-admin users cannot delete recurrent items")
        void deleteNotAdminFails() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

            expectNotAdmin(() ->
                recurrentItemService.delete(FakeDataServiceImpl.USER_ID_USER, recurrentItemId));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, 
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }

        @Test
        @DisplayName("Admin users can delete recurrent items")
        void deleteSucceeds() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

            FormResult result = recurrentItemService.delete(FakeDataServiceImpl.USER_ID_ADMIN, recurrentItemId);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("RecurrentItemServiceImplTest-testDelete_OK-recurrentItems.json", getClass(), initialRecurrentItems,
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }
    }

    @Nested
    @DisplayName("Generate Ready Items Tests")
    class GenerateReadyTests {

        @Test
        @DisplayName("No items are generated when none are ready")
        void generateReadyNone() {
            String initialItems = JsonTools.prettyPrintWithoutNulls(itemDao.findAll(Sort.by("invoiceId", "description")));
            String initialRecurrentItems = JsonTools.prettyPrintWithoutNulls(recurrentItemDao.findAll(Sort.by("client.name", "description")));

            recurrentItemService.generateReady(DateTools.parseFull("2019-06-30 23:59:00"));

            String finalItems = JsonTools.prettyPrintWithoutNulls(itemDao.findAll(Sort.by("invoiceId", "description")));
            String finalRecurrentItems = JsonTools.prettyPrintWithoutNulls(recurrentItemDao.findAll(Sort.by("client.name", "description")));
            Assertions.assertEquals(initialItems, finalItems);
            Assertions.assertEquals(initialRecurrentItems, finalRecurrentItems);
        }

        @Test
        @DisplayName("Items are generated when they are ready")
        void generateReadySucceeds() {
            recurrentItemService.generateReady(DateTools.parseFull("2019-07-01 00:45:00"));

            AssertTools.assertJsonComparisonWithoutNulls("RecurrentItemServiceImplTest-testGenerateReady_ok-items.json", getClass(), 
                    trimItem(itemDao.findAll(Sort.by("invoiceId", "description"))));
            AssertTools.assertJsonComparisonWithoutNulls("RecurrentItemServiceImplTest-testGenerateReady_ok-recurrentItems.json", getClass(),
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("client.name", "description"))));
        }
    }

    @Nested
    @DisplayName("Update Recurrent Item Tests")
    class UpdateRecurrentItemTests {

        @Test
        @DisplayName("Non-admin users cannot update recurrent items")
        void updateNotAdminFails() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

            CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                    .setCalendarUnit(Calendar.MONTH)
                    .setDelta(1)
                    .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                    .setDescription("Hosting L1")
                    .setPrice(500)
                    .setCategory("hosting");

            expectNotAdmin(() ->
                recurrentItemService.update(FakeDataServiceImpl.USER_ID_USER, recurrentItemId, form));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, 
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }

        @Test
        @DisplayName("Admin users can update recurrent items")
        void updateSucceeds() {
            List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

            long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

            CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                    .setCalendarUnit(Calendar.MONTH)
                    .setDelta(1)
                    .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                    .setDescription("Hosting L1")
                    .setPrice(500)
                    .setCategory("hosting");

            FormResult result = recurrentItemService.update(FakeDataServiceImpl.USER_ID_ADMIN, recurrentItemId, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("RecurrentItemServiceImplTest-testUpdate_OK-recurrentItems.json", getClass(), initialRecurrentItems,
                    trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));
        }
    }

}

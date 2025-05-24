package com.foilen.crm.services;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.CreateOrUpdateRecurrentItemForm;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.JsonTools;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Calendar;
import java.util.List;

public class RecurrentItemServiceImplTest extends AbstractSpringTests {

    @Autowired
    private RecurrentItemService recurrentItemService;

    public RecurrentItemServiceImplTest() {
        super(true);
    }

    @Test
    public void testCreate_notAdmin_FAIL() {

        List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

        CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                .setCalendarUnit(Calendar.MONTH)
                .setDelta(1)
                .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                .setDescription("Hosting L1")
                .setPrice(500)
                .setCategory("hosting");

        expectNotAdmin(() -> {
            recurrentItemService.create(FakeDataServiceImpl.USER_ID_USER, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));

    }

    @Test
    public void testCreate_OK() {

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

    @Test
    public void testDelete_notAdmin_FAIL() {

        List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

        long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

        expectNotAdmin(() -> {
            recurrentItemService.delete(FakeDataServiceImpl.USER_ID_USER, recurrentItemId);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));

    }

    @Test
    public void testDelete_OK() {

        List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

        long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

        FormResult result = recurrentItemService.delete(FakeDataServiceImpl.USER_ID_ADMIN, recurrentItemId);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("RecurrentItemServiceImplTest-testDelete_OK-recurrentItems.json", getClass(), initialRecurrentItems,
                trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));

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

    @Test
    public void testUpdate_notAdmin_FAIL() {

        List<?> initialRecurrentItems = trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id")));

        long recurrentItemId = recurrentItemDao.findAllByClientShortName("avez").getFirst().getId();

        CreateOrUpdateRecurrentItemForm form = new CreateOrUpdateRecurrentItemForm()
                .setCalendarUnit(Calendar.MONTH)
                .setDelta(1)
                .setNextGenerationDate("2021-01-01").setClientShortName("avez")
                .setDescription("Hosting L1")
                .setPrice(500)
                .setCategory("hosting");

        expectNotAdmin(() -> {
            recurrentItemService.update(FakeDataServiceImpl.USER_ID_USER, recurrentItemId, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialRecurrentItems, trimRecurrentItem(recurrentItemDao.findAll(Sort.by("id"))));

    }

    @Test
    public void testUpdate_OK() {

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

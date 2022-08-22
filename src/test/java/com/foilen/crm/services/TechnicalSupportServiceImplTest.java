/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

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
import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;

public class TechnicalSupportServiceImplTest extends AbstractSpringTests {

    @Autowired
    private TechnicalSupportService technicalSupportService;

    public TechnicalSupportServiceImplTest() {
        super(true);
    }

    @Test
    public void testCreate_notAdmin_FAIL() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid("N1") //
                .setPricePerHour(1099);

        expectNotAdmin(() -> {
            technicalSupportService.create(FakeDataServiceImpl.USER_ID_USER, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testCreate_OK() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid("N1") //
                .setPricePerHour(1099);

        FormResult result = technicalSupportService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testCreate_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testCreate_sid_exists_FAIL() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid("S1") //
                .setPricePerHour(1099);

        FormResult result = technicalSupportService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-sid_exists_FAIL-FormResult.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testDelete_notAdmin_FAIL() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        expectNotAdmin(() -> {
            technicalSupportService.delete(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.SID_1);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testDelete_OK() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        FormResult result = technicalSupportService.delete(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testDelete_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testListAll_notAdmin_FAIL() {
        expectNotAdmin(() -> {
            technicalSupportService.listAll(FakeDataServiceImpl.USER_ID_USER, 1, null);
        });
    }

    @Test
    public void testListAll_OK() {
        AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-testListAll_OK.json", getClass(), technicalSupportService.listAll(FakeDataServiceImpl.USER_ID_ADMIN, 1, null));
    }

    @Test
    public void testUpdate_noChange_OK() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid(FakeDataServiceImpl.SID_1) //
                .setPricePerHour(1000);

        FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testUpdate_notAdmin_FAIL() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid(FakeDataServiceImpl.SID_1) //
                .setPricePerHour(1234);

        expectNotAdmin(() -> {
            technicalSupportService.update(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.SID_1, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testUpdate_OK() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid(FakeDataServiceImpl.SID_1) //
                .setPricePerHour(1234);

        FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testUpdate_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

    @Test
    public void testUpdate_sid_exists_FAIL() {

        List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

        CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm() //
                .setSid(FakeDataServiceImpl.SID_2) //
                .setPricePerHour(1234);

        FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
        AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-sid_exists_FAIL-FormResult.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));

    }

}

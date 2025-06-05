package com.foilen.crm.services;

import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.test.AbstractSpringTests;
import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@DisplayName("Technical Support Service Implementation Tests")
public class TechnicalSupportServiceImplTest extends AbstractSpringTests {

    @Autowired
    private TechnicalSupportService technicalSupportService;

    public TechnicalSupportServiceImplTest() {
        super(true);
    }

    @Nested
    @DisplayName("Create Technical Support Tests")
    class CreateTechnicalSupportTests {

        @Test
        @DisplayName("Non-admin users cannot create technical support")
        void createNotAdminFails() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid("N1")
                    .setPricePerHour(1099);

            expectNotAdmin(() ->
                technicalSupportService.create(FakeDataServiceImpl.USER_ID_USER, form));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Admin users can create technical support")
        void createSucceeds() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid("N1")
                    .setPricePerHour(1099);

            FormResult result = technicalSupportService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testCreate_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Cannot create technical support with existing SID")
        void createWithExistingSidFails() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid("S1")
                    .setPricePerHour(1099);

            FormResult result = technicalSupportService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
            AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-sid_exists_FAIL-FormResult.json", getClass(), result);

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }
    }

    @Nested
    @DisplayName("Delete Technical Support Tests")
    class DeleteTechnicalSupportTests {

        @Test
        @DisplayName("Non-admin users cannot delete technical support")
        void deleteNotAdminFails() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            expectNotAdmin(() ->
                technicalSupportService.delete(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.SID_1));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Admin users can delete technical support")
        void deleteSucceeds() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            FormResult result = technicalSupportService.delete(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testDelete_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }
    }

    @Nested
    @DisplayName("List Technical Support Tests")
    class ListTechnicalSupportTests {

        @Test
        @DisplayName("Non-admin users cannot list technical support")
        void listAllNotAdminFails() {
            expectNotAdmin(() ->
                technicalSupportService.listAll(FakeDataServiceImpl.USER_ID_USER, 1, null));
        }

        @Test
        @DisplayName("Admin users can list all technical support")
        void listAllSucceeds() {
            AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-testListAll_OK.json", getClass(), 
                    technicalSupportService.listAll(FakeDataServiceImpl.USER_ID_ADMIN, 1, null));
        }
    }

    @Nested
    @DisplayName("Update Technical Support Tests")
    class UpdateTechnicalSupportTests {

        @Test
        @DisplayName("Update with no changes succeeds")
        void updateNoChangeSucceeds() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid(FakeDataServiceImpl.SID_1)
                    .setPricePerHour(1000);

            FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Non-admin users cannot update technical support")
        void updateNotAdminFails() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid(FakeDataServiceImpl.SID_1)
                    .setPricePerHour(1234);

            expectNotAdmin(() ->
                technicalSupportService.update(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.SID_1, form));

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Admin users can update technical support")
        void updateSucceeds() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid(FakeDataServiceImpl.SID_1)
                    .setPricePerHour(1234);

            FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
            AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

            AssertTools.assertDiffJsonComparison("TechnicalSupportServiceImplTest-testUpdate_OK-technicalSupports.json", getClass(), initialTechnicalSupports,
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }

        @Test
        @DisplayName("Cannot update to an existing SID")
        void updateWithExistingSidFails() {
            List<?> initialTechnicalSupports = trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid")));

            CreateOrUpdateTechnicalSupportForm form = new CreateOrUpdateTechnicalSupportForm()
                    .setSid(FakeDataServiceImpl.SID_2)
                    .setPricePerHour(1234);

            FormResult result = technicalSupportService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.SID_1, form);
            AssertTools.assertJsonComparisonWithoutNulls("TechnicalSupportServiceImplTest-sid_exists_FAIL-FormResult.json", getClass(), result);

            AssertTools.assertDiffJsonComparison(new AssertDiff(), initialTechnicalSupports, 
                    trimTechnicalSupport(technicalSupportDao.findAll(Sort.by("sid"))));
        }
    }

}

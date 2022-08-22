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
import com.foilen.crm.web.model.CreateOrUpdateClientForm;
import com.foilen.smalltools.restapi.model.FormResult;
import com.foilen.smalltools.test.asserts.AssertDiff;
import com.foilen.smalltools.test.asserts.AssertTools;

public class ClientServiceImplTest extends AbstractSpringTests {

    @Autowired
    private ClientService clientService;

    public ClientServiceImplTest() {
        super(true);
    }

    @Test
    public void testCreate_notAdmin_FAIL() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("ABC")//
                .setShortName("abc") //
                .setContactName("Abc Def").setEmail("abc@example.com") //
                .setAddress("555 Betancour").setTel("555-202-0101") //
                .setMainSite("http://abc.here.com") //
                .setLang("EN") //
                .setTechnicalSupportSid("S2");

        expectNotAdmin(() -> {
            clientService.create(FakeDataServiceImpl.USER_ID_USER, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testCreate_OK() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("ABC")//
                .setShortName("abc") //
                .setContactName("Abc Def").setEmail("abc@example.com") //
                .setAddress("555 Betancour").setTel("555-202-0101") //
                .setMainSite("http://abc.here.com") //
                .setLang("EN") //
                .setTechnicalSupportSid("S2");

        FormResult result = clientService.create(FakeDataServiceImpl.USER_ID_ADMIN, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ClientServiceImplTest-testCreate_OK-clients.json", getClass(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testCreate_shortName_exists_FAIL() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("ABC")//
                .setShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_EXTRA) //
                .setContactName("Abc Def").setEmail("abc@example.com") //
                .setAddress("555 Betancour").setTel("555-202-0101") //
                .setMainSite("http://abc.here.com") //
                .setLang("EN") //
                .setTechnicalSupportSid("S2");

        FormResult result = clientService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        AssertTools.assertJsonComparisonWithoutNulls("ClientServiceImplTest-shortName_exists_FAIL-FormResult.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testDelete_notAdmin_FAIL() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        expectNotAdmin(() -> {
            clientService.delete(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testDelete_OK() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        FormResult result = clientService.delete(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ClientServiceImplTest-testDelete_OK-clients.json", getClass(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testListAll_notAdmin_FAIL() {
        expectNotAdmin(() -> {
            clientService.listAll(FakeDataServiceImpl.USER_ID_USER, 1, null);
        });
    }

    @Test
    public void testListAll_OK() {
        AssertTools.assertJsonComparisonWithoutNulls("ClientServiceImplTest-testListAll_OK.json", getClass(), clientService.listAll(FakeDataServiceImpl.USER_ID_ADMIN, 1, null));
    }

    @Test
    public void testUpdate_noChange_OK() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("Bazar")//
                .setShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR) //
                .setContactName("Benoit Bezos").setEmail("benoit@example.com") //
                .setAddress("1010 Betancour").setTel("555-101-0101") //
                .setMainSite("http://bazar.example.com") //
                .setLang("FR") //
                .setTechnicalSupportSid("S1");

        FormResult result = clientService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testUpdate_notAdmin_FAIL() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("Bazar Yay")//
                .setShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR) //
                .setContactName("Benoit Banana").setEmail("benoit@example2.com") //
                .setAddress("1020 Betancour").setTel("555-202-0101") //
                .setMainSite("http://bazar.here.com") //
                .setLang("EN") //
                .setTechnicalSupportSid("S2");

        expectNotAdmin(() -> {
            clientService.update(FakeDataServiceImpl.USER_ID_USER, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        });

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testUpdate_OK() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("Bazar Yay")//
                .setShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR) //
                .setContactName("Benoit Banana").setEmail("benoit@example2.com") //
                .setAddress("1020 Betancour").setTel("555-202-0101") //
                .setMainSite("http://bazar.here.com") //
                .setLang("EN") //
                .setTechnicalSupportSid("S2");

        FormResult result = clientService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ClientServiceImplTest-testUpdate_OK-clients.json", getClass(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testUpdate_shortName_exists_FAIL() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("Bazar")//
                .setShortName(FakeDataServiceImpl.CLIENT_SHORTNAME_EXTRA) //
                .setContactName("Benoit Bezos").setEmail("benoit@example.com") //
                .setAddress("1010 Betancour").setTel("555-101-0101") //
                .setMainSite("http://bazar.example.com") //
                .setLang("FR") //
                .setTechnicalSupportSid("S1");

        FormResult result = clientService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        AssertTools.assertJsonComparisonWithoutNulls("ClientServiceImplTest-shortName_exists_FAIL-FormResult.json", getClass(), result);

        AssertTools.assertDiffJsonComparison(new AssertDiff(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

    @Test
    public void testUpdate_shortName_OK() {

        List<?> initialClients = trimClient(clientDao.findAll(Sort.by("shortName")));

        CreateOrUpdateClientForm form = new CreateOrUpdateClientForm() //
                .setName("Bazar")//
                .setShortName("bbb") //
                .setContactName("Benoit Bezos").setEmail("benoit@example.com") //
                .setAddress("1010 Betancour").setTel("555-101-0101") //
                .setMainSite("http://bazar.example.com") //
                .setLang("FR") //
                .setTechnicalSupportSid("S1");

        FormResult result = clientService.update(FakeDataServiceImpl.USER_ID_ADMIN, FakeDataServiceImpl.CLIENT_SHORTNAME_BAZAR, form);
        AssertTools.assertJsonComparisonWithoutNulls("FormResult-success.json", getClass(), result);

        AssertTools.assertDiffJsonComparison("ClientServiceImplTest-testUpdate_shortName_OK-clients.json", getClass(), initialClients, trimClient(clientDao.findAll(Sort.by("shortName"))));

    }

}

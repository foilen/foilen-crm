/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.test;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.foilen.crm.CrmApp;
import com.foilen.crm.CrmConfig;
import com.foilen.crm.CrmSpringConfig;
import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.RecurrentItemDao;
import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.localonly.FakeDataService;
import com.foilen.crm.web.model.ClientShort;
import com.foilen.login.spring.client.security.FoilenAuthentication;
import com.foilen.login.spring.client.security.FoilenLoginUserDetails;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.SecureRandomTools;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CrmTestConfig.class, CrmSpringConfig.class })
@ActiveProfiles("JUNIT")
public abstract class AbstractSpringTests {

    @Autowired
    protected FakeDataService fakeDataService;
    @Autowired
    protected ClientDao clientDao;
    @Autowired
    protected ItemDao itemDao;
    @Autowired
    protected RecurrentItemDao recurrentItemDao;
    @Autowired
    protected TechnicalSupportDao technicalSupportDao;
    @Autowired
    protected TransactionDao transactionDao;
    @Autowired
    protected UserDao userDao;

    private boolean createFakeData;

    public AbstractSpringTests(boolean createFakeData) {
        CrmConfig crmConfig = new CrmConfig();
        crmConfig.setBaseUrl("https://crm.example.com");
        crmConfig.setMysqlDatabaseUserName("_MYSQL_USER_NAME_");
        crmConfig.setMysqlDatabasePassword("_MYSQL_PASSWORD_");
        crmConfig.setMailFrom("crm@example.com");
        crmConfig.setCompany("MyCompany");
        crmConfig.getLoginConfigDetails().setBaseUrl("http://login.example.com");
        crmConfig.setLoginCookieSignatureSalt(SecureRandomTools.randomBase64String(10));
        CrmApp.configToSystemProperties(crmConfig);

        System.setProperty("MODE", "JUNIT");
        this.createFakeData = createFakeData;
    }

    @Before
    public void createFakeData() {

        // Set no-one
        SecurityContextHolder.clearContext();

        fakeDataService.clearAll();
        if (createFakeData) {
            fakeDataService.createAll();
        }
    }

    protected void setFoilenAuth(String userId, String email) {
        SecurityContext securityContext = new SecurityContextImpl();
        UserDetails userDetails = new FoilenLoginUserDetails(userId, email);
        securityContext.setAuthentication(new FoilenAuthentication(userDetails));
        SecurityContextHolder.setContext(securityContext);
    }

    protected List<com.foilen.crm.web.model.Item> trimItem(List<Item> entities) {
        return entities.stream() //
                .map(e -> {
                    com.foilen.crm.web.model.Item t = JsonTools.clone(e, com.foilen.crm.web.model.Item.class);
                    t.setClient(JsonTools.clone(e.getClient(), ClientShort.class));
                    t.setId(null);
                    t.setDate(null);
                    return t;
                }) //
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.RecurrentItem> trimRecurrentItem(List<RecurrentItem> entities) {
        return entities.stream() //
                .map(e -> {
                    com.foilen.crm.web.model.RecurrentItem t = JsonTools.clone(e, com.foilen.crm.web.model.RecurrentItem.class);
                    t.setId(null);
                    t.setClient(JsonTools.clone(e.getClient(), ClientShort.class));
                    return t;
                }) //
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.Transaction> trimTransaction(List<Transaction> entities) {
        return entities.stream() //
                .map(e -> {
                    com.foilen.crm.web.model.Transaction t = JsonTools.clone(e, com.foilen.crm.web.model.Transaction.class);
                    t.setClient(JsonTools.clone(e.getClient(), ClientShort.class));
                    t.setDate(null);
                    return t;
                }) //
                .collect(Collectors.toList());
    }

}

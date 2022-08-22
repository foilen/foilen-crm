/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.localonly;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foilen.crm.db.dao.ClientDao;
import com.foilen.crm.db.dao.ItemDao;
import com.foilen.crm.db.dao.RecurrentItemDao;
import com.foilen.crm.db.dao.TechnicalSupportDao;
import com.foilen.crm.db.dao.TransactionDao;
import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.db.entities.invoice.RecurrentItem;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.db.entities.user.User;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.DateTools;

@Service
@Transactional
public class FakeDataServiceImpl extends AbstractBasics implements FakeDataService {

    public static final String USER_ID_ADMIN = "111111";
    public static final String USER_ID_USER = "222222";
    public static final String USER_ID_TEST_1 = "333333";

    public static final String CLIENT_SHORTNAME_BAZAR = "bazar";
    public static final String CLIENT_SHORTNAME_EXTRA = "extra";
    public static final String SID_1 = "S1";
    public static final String SID_2 = "S2";

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RecurrentItemDao recurrentItemDao;
    @Autowired
    private TechnicalSupportDao technicalSupportDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private UserDao userDao;

    @Override
    public void clearAll() {

        logger.info("Begin CLEAR ALL");

        itemDao.deleteAll();
        recurrentItemDao.deleteAll();
        transactionDao.deleteAll();
        clientDao.deleteAll();
        technicalSupportDao.deleteAll();
        userDao.deleteAll();

        itemDao.flush();
        recurrentItemDao.flush();
        transactionDao.flush();
        clientDao.flush();
        technicalSupportDao.flush();
        userDao.flush();

        logger.info("End CLEAR ALL");
    }

    @Override
    public void createAll() {

        logger.info("Begin CREATE ALL");

        createUsers();
        createTechnicalSupports();
        createClients();
        createItems();
        createTransactions();
        createRecurrentItems();

        logger.info("End CREATE ALL");
    }

    private void createClients() {

        logger.info("createClients");

        TechnicalSupport s1 = technicalSupportDao.findBySid(SID_1);
        TechnicalSupport s2 = technicalSupportDao.findBySid(SID_2);

        clientDao.saveAndFlush(new Client() //
                .setName("Bazar").setShortName(CLIENT_SHORTNAME_BAZAR) //
                .setContactName("Benoit Bezos").setEmail("benoit@example.com") //
                .setAddress("1010 Betancour").setTel("555-101-0101") //
                .setMainSite("http://bazar.example.com") //
                .setLang("FR").setTechnicalSupport(s1));
        clientDao.saveAndFlush(new Client() //
                .setName("Avez").setShortName("avez") //
                .setContactName("Alex Aubut").setEmail("alex@example.com") //
                .setAddress("2500 Alegria").setTel("555-202-0202") //
                .setMainSite("http://avez.example.com") //
                .setLang("EN").setTechnicalSupport(s2));
        clientDao.saveAndFlush(new Client() //
                .setName("Zoo Alphonce").setShortName("zooa") //
                .setContactName("Zoe Zephir").setEmail("zeo@example.com") //
                .setAddress("300 Zenith").setTel("555-303-0303") //
                .setMainSite("http://zoo.example.com") //
                .setLang("FR").setTechnicalSupport(s2));
        clientDao.saveAndFlush(new Client() //
                .setName("Extra Vanilla").setShortName(CLIENT_SHORTNAME_EXTRA) //
                .setContactName("Extra Vanilla").setEmail("extra@example.com") //
                .setAddress("300 Zenith").setTel("555-303-4444") //
                .setMainSite("http://extra.example.com") //
                .setLang("FR"));

    }

    private void createItems() {

        logger.info("createItems");

        Client clientAvez = clientDao.findByShortName("avez");
        Client clientBazar = clientDao.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientExtra = clientDao.findByShortName(CLIENT_SHORTNAME_EXTRA);
        Client clientZooa = clientDao.findByShortName("zooa");

        // Pending
        itemDao.save(new Item(clientAvez, null, DateTools.parseDateOnly("2019-06-01"), "Shared hosting - L1", 500, "hosting"));
        itemDao.save(new Item(clientAvez, null, DateTools.parseDateOnly("2019-06-05"), "Install Wordpress", 2000, "consulting"));
        itemDao.save(new Item(clientBazar, null, DateTools.parseDateOnly("2019-06-02"), "Shared hosting - L1", 500, "hosting"));
        itemDao.save(new Item(clientZooa, null, DateTools.parseDateOnly("2019-06-01"), "Shared hosting - L2", 1000, "hosting"));

        // Billed
        itemDao.save(new Item(clientAvez, "I190601-1", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L1", 500, "hosting"));
        itemDao.save(new Item(clientBazar, "I190601-2", DateTools.parseDateOnly("2019-05-05"), "Install Wordpress", 2000, "consulting"));
        itemDao.save(new Item(clientBazar, "I190601-2", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L1", 500, "hosting"));
        itemDao.save(new Item(clientZooa, "I190601-3", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L2", 1000, "hosting"));

        for (int i = 1; i <= 12; ++i) {
            String textMonth = String.valueOf(i);
            if (textMonth.length() == 1) {
                textMonth = "0" + textMonth;
            }
            itemDao.save(new Item(clientExtra, "I19" + textMonth + "01-5", DateTools.parseDateOnly("2019-" + textMonth + "-01"), "Delivery", 1000, "delivery"));
        }

    }

    private void createRecurrentItems() {

        logger.info("createRecurrentItems");

        Client clientAvez = clientDao.findByShortName("avez");
        Client clientBazar = clientDao.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientZooa = clientDao.findByShortName("zooa");

        recurrentItemDao.save(new RecurrentItem(clientAvez, "Shared hosting - L1", 500, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));
        recurrentItemDao.save(new RecurrentItem(clientBazar, "Shared hosting - L1", 500, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));
        recurrentItemDao.save(new RecurrentItem(clientZooa, "Shared hosting - L2", 1000, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));

    }

    private void createTechnicalSupports() {
        logger.info("createTechnicalSupports");

        technicalSupportDao.saveAndFlush(new TechnicalSupport(SID_1, 1000));
        technicalSupportDao.saveAndFlush(new TechnicalSupport(SID_2, 2000));

    }

    private void createTransactions() {

        logger.info("createTransactions");

        Client clientAvez = clientDao.findByShortName("avez");
        Client clientBazar = clientDao.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientExtra = clientDao.findByShortName(CLIENT_SHORTNAME_EXTRA);
        Client clientZooa = clientDao.findByShortName("zooa");

        transactionDao.save(new Transaction(clientAvez, "I190601-1", DateTools.parseDateOnly("2019-06-01"), "Invoice I190601-1", 500));
        transactionDao.save(new Transaction(clientBazar, "I190601-2", DateTools.parseDateOnly("2019-06-01"), "Facture I190601-2", 2500));
        transactionDao.save(new Transaction(clientZooa, "I190601-3", DateTools.parseDateOnly("2019-06-01"), "Facture I190601-3", 1000));

        for (int i = 1; i <= 12; ++i) {
            String textMonth = String.valueOf(i);
            if (textMonth.length() == 1) {
                textMonth = "0" + textMonth;
            }
            transactionDao.save(new Transaction(clientExtra, "I19" + textMonth + "01-5", DateTools.parseDateOnly("2019-" + textMonth + "-01"), "Facture I19" + textMonth + "01-5", 1000));
        }

    }

    private void createUsers() {
        logger.info("createUsers");

        userDao.saveAndFlush(new User(USER_ID_ADMIN, true));
        userDao.saveAndFlush(new User(USER_ID_USER, false));
        userDao.saveAndFlush(new User(USER_ID_TEST_1, false));
        userDao.saveAndFlush(new User("444444", false));

    }

}

package com.foilen.crm.localonly;

import com.foilen.crm.db.repository.*;
import com.foilen.crm.db.entities.invoice.*;
import com.foilen.crm.db.entities.user.User;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

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
    private ClientRepository clientRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RecurrentItemRepository recurrentItemRepository;
    @Autowired
    private TechnicalSupportRepository technicalSupportRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void clearAll() {

        logger.info("Begin CLEAR ALL");

        itemRepository.deleteAll();
        recurrentItemRepository.deleteAll();
        transactionRepository.deleteAll();
        clientRepository.deleteAll();
        technicalSupportRepository.deleteAll();
        userRepository.deleteAll();

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

        TechnicalSupport s1 = technicalSupportRepository.findBySid(SID_1);
        TechnicalSupport s2 = technicalSupportRepository.findBySid(SID_2);

        clientRepository.save(new Client()
                .setName("Bazar").setShortName(CLIENT_SHORTNAME_BAZAR)
                .setContactName("Benoit Bezos").setEmail("benoit@example.com")
                .setAddress("1010 Betancour").setTel("555-101-0101")
                .setMainSite("http://bazar.example.com")
                .setLang("FR").setTechnicalSupportId(s1.getId()));
        clientRepository.save(new Client()
                .setName("Avez").setShortName("avez")
                .setContactName("Alex Aubut").setEmail("alex@example.com")
                .setAddress("2500 Alegria").setTel("555-202-0202")
                .setMainSite("http://avez.example.com")
                .setLang("EN").setTechnicalSupportId(s2.getId()));
        clientRepository.save(new Client()
                .setName("Zoo Alphonce").setShortName("zooa")
                .setContactName("Zoe Zephir").setEmail("zeo@example.com")
                .setAddress("300 Zenith").setTel("555-303-0303")
                .setMainSite("http://zoo.example.com")
                .setLang("FR").setTechnicalSupportId(s2.getId()));
        clientRepository.save(new Client()
                .setName("Extra Vanilla").setShortName(CLIENT_SHORTNAME_EXTRA)
                .setContactName("Extra Vanilla").setEmail("extra@example.com")
                .setAddress("300 Zenith").setTel("555-303-4444")
                .setMainSite("http://extra.example.com")
                .setLang("FR"));

    }

    private void createItems() {

        logger.info("createItems");

        Client clientAvez = clientRepository.findByShortName("avez");
        Client clientBazar = clientRepository.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientExtra = clientRepository.findByShortName(CLIENT_SHORTNAME_EXTRA);
        Client clientZooa = clientRepository.findByShortName("zooa");

        // Pending
        itemRepository.save(new Item(clientAvez.getId(), null, DateTools.parseDateOnly("2019-06-01"), "Shared hosting - L1", 500, "hosting"));
        itemRepository.save(new Item(clientAvez.getId(), null, DateTools.parseDateOnly("2019-06-05"), "Install Wordpress", 2000, "consulting"));
        itemRepository.save(new Item(clientBazar.getId(), null, DateTools.parseDateOnly("2019-06-02"), "Shared hosting - L1", 500, "hosting"));
        itemRepository.save(new Item(clientZooa.getId(), null, DateTools.parseDateOnly("2019-06-01"), "Shared hosting - L2", 1000, "hosting"));

        // Billed
        itemRepository.save(new Item(clientAvez.getId(), "I190601-1", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L1", 500, "hosting"));
        itemRepository.save(new Item(clientBazar.getId(), "I190601-2", DateTools.parseDateOnly("2019-05-05"), "Install Wordpress", 2000, "consulting"));
        itemRepository.save(new Item(clientBazar.getId(), "I190601-2", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L1", 500, "hosting"));
        itemRepository.save(new Item(clientZooa.getId(), "I190601-3", DateTools.parseDateOnly("2019-05-01"), "Shared hosting - L2", 1000, "hosting"));

        for (int i = 1; i <= 12; ++i) {
            String textMonth = String.valueOf(i);
            if (textMonth.length() == 1) {
                textMonth = "0" + textMonth;
            }
            itemRepository.save(new Item(clientExtra.getId(), "I19" + textMonth + "01-5", DateTools.parseDateOnly("2019-" + textMonth + "-01"), "Delivery", 1000, "delivery"));
        }

    }

    private void createRecurrentItems() {

        logger.info("createRecurrentItems");

        Client clientAvez = clientRepository.findByShortName("avez");
        Client clientBazar = clientRepository.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientZooa = clientRepository.findByShortName("zooa");

        recurrentItemRepository.save(new RecurrentItem(clientAvez.getId(), "Shared hosting - L1", 500, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));
        recurrentItemRepository.save(new RecurrentItem(clientBazar.getId(), "Shared hosting - L1", 500, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));
        recurrentItemRepository.save(new RecurrentItem(clientZooa.getId(), "Shared hosting - L2", 1000, "hosting", Calendar.MONTH, 1, DateTools.parseDateOnly("2019-07-01")));

    }

    private void createTechnicalSupports() {
        logger.info("createTechnicalSupports");

        technicalSupportRepository.save(new TechnicalSupport(SID_1, 1000));
        technicalSupportRepository.save(new TechnicalSupport(SID_2, 2000));

    }

    private void createTransactions() {

        logger.info("createTransactions");

        Client clientAvez = clientRepository.findByShortName("avez");
        Client clientBazar = clientRepository.findByShortName(CLIENT_SHORTNAME_BAZAR);
        Client clientExtra = clientRepository.findByShortName(CLIENT_SHORTNAME_EXTRA);
        Client clientZooa = clientRepository.findByShortName("zooa");

        transactionRepository.save(new Transaction(clientAvez.getId(), "I190601-1", DateTools.parseDateOnly("2019-06-01"), "Invoice I190601-1", 500));
        transactionRepository.save(new Transaction(clientBazar.getId(), "I190601-2", DateTools.parseDateOnly("2019-06-01"), "Facture I190601-2", 2500));
        transactionRepository.save(new Transaction(clientZooa.getId(), "I190601-3", DateTools.parseDateOnly("2019-06-01"), "Facture I190601-3", 1000));

        for (int i = 1; i <= 12; ++i) {
            String textMonth = String.valueOf(i);
            if (textMonth.length() == 1) {
                textMonth = "0" + textMonth;
            }
            transactionRepository.save(new Transaction(clientExtra.getId(), "I19" + textMonth + "01-5", DateTools.parseDateOnly("2019-" + textMonth + "-01"), "Facture I19" + textMonth + "01-5", 1000));
        }

    }

    private void createUsers() {
        logger.info("createUsers");

        userRepository.save(new User(USER_ID_ADMIN, true));
        userRepository.save(new User(USER_ID_USER, false));
        userRepository.save(new User(USER_ID_TEST_1, false));
        userRepository.save(new User("444444", false));

    }

}

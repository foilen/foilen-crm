package com.foilen.crm.test;

import com.foilen.crm.CrmApp;
import com.foilen.crm.CrmConfig;
import com.foilen.crm.CrmSpringConfig;
import com.foilen.crm.CrmUpgradesSpringConfig;
import com.foilen.crm.db.repository.*;
import com.foilen.crm.db.entities.invoice.*;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.crm.localonly.EmbeddedMongoDbSpringConfig;
import com.foilen.crm.localonly.FakeDataService;
import com.foilen.crm.web.model.ClientShort;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.SecureRandomTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.stream.Collectors;

@SpringJUnitConfig(classes = {CrmTestConfig.class, CrmSpringConfig.class, EmbeddedMongoDbSpringConfig.class, CrmUpgradesSpringConfig.class})
@ActiveProfiles("JUNIT")
public abstract class AbstractSpringTests {

    private final boolean createFakeData;
    @Autowired
    protected FakeDataService fakeDataService;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected RecurrentItemRepository recurrentItemRepository;
    @Autowired
    protected TechnicalSupportRepository technicalSupportRepository;
    @Autowired
    protected TransactionRepository transactionRepository;
    @Autowired
    protected UserRepository userRepository;

    public AbstractSpringTests(boolean createFakeData) {
        CrmConfig crmConfig = new CrmConfig();
        crmConfig.setBaseUrl("https://crm.example.com");
        crmConfig.setMailFrom("crm@example.com");
        crmConfig.setCompany("MyCompany");
        crmConfig.setLoginCookieSignatureSalt(SecureRandomTools.randomBase64String(10));
        CrmApp.configToSystemProperties(crmConfig);

        System.setProperty("MODE", "JUNIT");
        this.createFakeData = createFakeData;
    }

    @BeforeEach
    public void createFakeData() {

        // Set no-one
        SecurityContextHolder.clearContext();

        fakeDataService.clearAll();
        if (createFakeData) {
            fakeDataService.createAll();
        }
    }

    /**
     * Asserts that the executable throws an ErrorMessageException with "error.notAdmin" message
     * when executed by a non-admin user.
     *
     * @param executable the code to execute that must throw an exception
     */
    protected void expectNotAdmin(Executable executable) {
        ErrorMessageException exception = Assertions.assertThrows(
                ErrorMessageException.class,
                executable,
                "Must throw ErrorMessageException for non-admin users"
        );
        Assertions.assertEquals("error.notAdmin", exception.getMessage(), "Exception must have 'error.notAdmin' message");
    }

    private ClientShort toClientShort(String clientId) {
        if (clientId == null) {
            return null;
        }
        Client client = clientRepository.findById(clientId).orElse(null);
        return client == null ? null : JsonTools.clone(client, ClientShort.class);
    }

    protected List<com.foilen.crm.web.model.ClientExtended> trimClient(List<Client> entities) {
        return entities.stream()
                .map(e -> {
                    com.foilen.crm.web.model.ClientExtended c = JsonTools.clone(e, com.foilen.crm.web.model.ClientExtended.class);
                    com.foilen.crm.web.model.TechnicalSupport technicalSupport = null;
                    if (e.getTechnicalSupportId() != null) {
                        TechnicalSupport entity = technicalSupportRepository.findById(e.getTechnicalSupportId()).orElse(null);
                        technicalSupport = entity == null ? null : JsonTools.clone(entity, com.foilen.crm.web.model.TechnicalSupport.class);
                    }
                    c.setTechnicalSupport(technicalSupport);
                    return c;
                })
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.Item> trimItem(List<Item> entities) {
        return entities.stream()
                .map(e -> {
                    com.foilen.crm.web.model.Item t = JsonTools.clone(e, com.foilen.crm.web.model.Item.class);
                    t.setClient(toClientShort(e.getClientId()));
                    t.setId(null);
                    t.setDate(null);
                    return t;
                })
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.RecurrentItem> trimRecurrentItem(List<RecurrentItem> entities) {
        return entities.stream()
                .map(e -> {
                    com.foilen.crm.web.model.RecurrentItem t = JsonTools.clone(e, com.foilen.crm.web.model.RecurrentItem.class);
                    t.setId(null);
                    t.setClient(toClientShort(e.getClientId()));
                    return t;
                })
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.TechnicalSupport> trimTechnicalSupport(List<TechnicalSupport> entities) {
        return entities.stream()
                .map(e -> {
                    com.foilen.crm.web.model.TechnicalSupport c = JsonTools.clone(e, com.foilen.crm.web.model.TechnicalSupport.class);
                    return c;
                })
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.Transaction> trimTransaction(List<Transaction> entities) {
        return entities.stream()
                .map(e -> {
                    com.foilen.crm.web.model.Transaction t = JsonTools.clone(e, com.foilen.crm.web.model.Transaction.class);
                    t.setId(null);
                    t.setClient(toClientShort(e.getClientId()));
                    t.setDate(null);
                    return t;
                })
                .collect(Collectors.toList());
    }

    protected List<com.foilen.crm.web.model.User> trimUser(List<User> entities) {
        return entities.stream()
                .map(e -> JsonTools.clone(e, com.foilen.crm.web.model.User.class))
                .collect(Collectors.toList());
    }

}

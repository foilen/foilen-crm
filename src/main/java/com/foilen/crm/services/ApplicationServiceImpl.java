package com.foilen.crm.services;

import com.foilen.crm.db.repository.ClientRepository;
import com.foilen.crm.db.repository.ItemRepository;
import com.foilen.crm.db.repository.TransactionRepository;
import com.foilen.crm.db.repository.UserRepository;
import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.ApplicationDetails;
import com.foilen.crm.web.model.ApplicationDetailsResult;
import com.foilen.crm.web.model.ReportBalanceByClient;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CloseableTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.ResourceTools;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBasics implements ApplicationService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    private Map<String, Object> translations = new TreeMap<>();

    private String version = "LOCAL";

    private void addTranslations(Map<String, String> lang, String filename) {
        filename = filename.substring(filename.indexOf('/'));
        Properties properties = new Properties();
        try {
            InputStream inputStream = ResourceTools.getResourceAsStream(filename);
            if (inputStream == null) {
                logger.error("Resource {} does not exist", filename);
                return;
            }
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            properties.forEach((key, value) -> lang.put((String) key, (String) value));
            CloseableTools.close(inputStream);
        } catch (IOException e) {
            logger.error("Could not load {}", filename, e);
        }

    }

    @Override
    public ApplicationDetailsResult getDetails(String userId) {

        ApplicationDetails applicationDetails = new ApplicationDetails()
                .setVersion(version)
                .setLang(LocaleContextHolder.getLocale().getLanguage())
                .setTranslations(translations);

        // Logged in user
        if (userId != null) {
            User user = userRepository.findByEmail(userId);
            if (user != null && !user.isDisabled()) {
                applicationDetails.setUserId(userId);
                applicationDetails.setUserEmail(user.getEmail());
                applicationDetails.setUserAdmin(user.isAdmin());
                applicationDetails.setUserHasPassword(user.getPasswordHash() != null);

                if (!user.isAdmin()) {
                    List<Client> clients = clientRepository.findAllByEmailIgnoreCase(user.getEmail());
                    Set<String> clientIds = clients.stream()
                            .map(Client::getId)
                            .collect(Collectors.toSet());

                    Map<String, Long> pendingTotalsByClientId = itemRepository.findPendingTotalsByClientIds(clientIds);

                    List<ReportBalanceByClient> clientBalances = clients.stream()
                            .map(client -> {
                                ReportBalanceByClient clientBalance = new ReportBalanceByClient(client.getName(),
                                        transactionRepository.findTotalByClientId(client.getId()));
                                clientBalance.setPendingTotal(pendingTotalsByClientId.getOrDefault(client.getId(), 0L));
                                return clientBalance;
                            })
                            .collect(Collectors.toList());

                    applicationDetails.setClientBalances(clientBalances);
                }
            }
        }

        return new ApplicationDetailsResult(applicationDetails);
    }

    @PostConstruct
    public void init() {

        // Version
        try {
            version = FileTools.getFileAsString("/app/version.txt");
        } catch (Exception e) {
        }

        // Translations
        Map<String, String> langEn = new TreeMap<>();
        translations.put("en", langEn);

        Map<String, String> langFr = new TreeMap<>();
        translations.put("fr", langFr);

        for (String basename : messageSource.getBasenameSet()) {
            addTranslations(langEn, basename + "_en.properties");
            addTranslations(langFr, basename + "_fr.properties");
        }

    }

}

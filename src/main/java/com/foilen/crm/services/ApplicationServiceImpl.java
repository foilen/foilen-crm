package com.foilen.crm.services;

import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.ApplicationDetails;
import com.foilen.crm.web.model.ApplicationDetailsResult;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CloseableTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.ResourceTools;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBasics implements ApplicationService {

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    private UserDao userDao;

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
                .setUserId(userId)
                .setLang(LocaleContextHolder.getLocale().getLanguage())
                .setTranslations(translations);
        ;

        // Logged in user
        User user = userDao.findByUserId(userId);
        if (user != null) {
            applicationDetails.setUserEmail(user.getEmail());
            applicationDetails.setUserAdmin(user.isAdmin());
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

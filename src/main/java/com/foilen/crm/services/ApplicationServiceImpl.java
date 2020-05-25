/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.foilen.crm.db.dao.UserDao;
import com.foilen.crm.db.entities.user.User;
import com.foilen.crm.web.model.ApplicationDetails;
import com.foilen.crm.web.model.ApplicationDetailsResult;
import com.foilen.login.spring.client.security.FoilenLoginUserDetails;
import com.foilen.login.spring.services.FoilenLoginService;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CharsetTools;
import com.foilen.smalltools.tools.CloseableTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.ResourceTools;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBasics implements ApplicationService {

    @Autowired
    private FoilenLoginService foilenLoginService;
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
            properties.load(new InputStreamReader(inputStream, CharsetTools.UTF_8));

            properties.forEach((key, value) -> lang.put((String) key, (String) value));
            CloseableTools.close(inputStream);
        } catch (IOException e) {
            logger.error("Could not load {}", filename, e);
        }

    }

    @Override
    public ApplicationDetailsResult getDetails(String userId) {

        ApplicationDetails applicationDetails = new ApplicationDetails() //
                .setVersion(version) //
                .setUserId(userId) //
                .setLang(LocaleContextHolder.getLocale().getLanguage()) //
                .setTranslations(translations);
        ;

        // Logged in user
        FoilenLoginUserDetails userDetails = foilenLoginService.getLoggedInUserDetails();
        if (userDetails != null) {
            applicationDetails.setUserEmail(userDetails.getEmail());

            User user = userDao.findByUserId(userDetails.getUsername());
            if (user != null) {
                applicationDetails.setUserAdmin(user.isAdmin());
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

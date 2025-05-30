package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.HashMap;
import java.util.Map;

public class ApplicationDetails extends AbstractApiBase {

    private String version;

    private String lang;

    private String userId;
    private String userEmail;
    private boolean userAdmin;

    private Map<String, Object> translations = new HashMap<>();

    public String getLang() {
        return lang;
    }

    public Map<String, Object> getTranslations() {
        return translations;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getVersion() {
        return version;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public ApplicationDetails setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public ApplicationDetails setTranslations(Map<String, Object> translations) {
        this.translations = translations;
        return this;
    }

    public ApplicationDetails setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
        return this;
    }

    public ApplicationDetails setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public ApplicationDetails setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ApplicationDetails setVersion(String version) {
        this.version = version;
        return this;
    }

}

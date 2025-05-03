package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ClientShort extends AbstractApiBase {

    private String name;
    private String shortName;
    private String email;

    // FR or EN
    private String lang;

    public String getEmail() {
        return email;
    }

    public String getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

}

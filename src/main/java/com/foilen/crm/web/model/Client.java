/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class Client extends AbstractApiBase {

    private String name;
    private String shortName;
    private String contactName;
    private String email;
    private String address;
    private String tel;

    private String mainSite;

    // FR or EN
    private String lang;

    private TechnicalSupport technicalSupport;

    public String getAddress() {
        return address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    public String getLang() {
        return lang;
    }

    public String getMainSite() {
        return mainSite;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public TechnicalSupport getTechnicalSupport() {
        return technicalSupport;
    }

    public String getTel() {
        return tel;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setMainSite(String mainSite) {
        this.mainSite = mainSite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTechnicalSupport(TechnicalSupport technicalSupport) {
        this.technicalSupport = technicalSupport;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}

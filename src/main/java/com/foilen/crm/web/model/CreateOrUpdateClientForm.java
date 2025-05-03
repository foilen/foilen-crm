package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class CreateOrUpdateClientForm extends AbstractApiBase {

    private String name;
    private String shortName;
    private String contactName;
    private String email;
    private String address;
    private String tel;

    private String mainSite;

    // FR or EN
    private String lang;

    private String technicalSupportSid;

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

    public String getTechnicalSupportSid() {
        return technicalSupportSid;
    }

    public String getTel() {
        return tel;
    }

    public CreateOrUpdateClientForm setAddress(String address) {
        this.address = address;
        return this;
    }

    public CreateOrUpdateClientForm setContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public CreateOrUpdateClientForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public CreateOrUpdateClientForm setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public CreateOrUpdateClientForm setMainSite(String mainSite) {
        this.mainSite = mainSite;
        return this;
    }

    public CreateOrUpdateClientForm setName(String name) {
        this.name = name;
        return this;
    }

    public CreateOrUpdateClientForm setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public CreateOrUpdateClientForm setTechnicalSupportSid(String technicalSupportSid) {
        this.technicalSupportSid = technicalSupportSid;
        return this;
    }

    public CreateOrUpdateClientForm setTel(String tel) {
        this.tel = tel;
        return this;
    }

}

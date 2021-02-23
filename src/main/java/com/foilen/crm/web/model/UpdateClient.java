package com.foilen.crm.web.model;

public class UpdateClient {

    private Long id;
    private String name;
    private String shortName;
    private String contactName;
    private String email;
    private String address;
    private String tel;

    private String mainSite;
    private String lang;
    private String technicalSupportSid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMainSite() {
        return mainSite;
    }

    public void setMainSite(String mainSite) {
        this.mainSite = mainSite;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTechnicalSupportSid() {
        return technicalSupportSid;
    }

    public void setTechnicalSupportSid(String technicalSupportSid) {
        this.technicalSupportSid = technicalSupportSid;
    }

    @Override
    public String toString() {
        return "UpdateClient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", mainSite='" + mainSite + '\'' +
                ", lang='" + lang + '\'' +
                ", technicalSupportSid='" + technicalSupportSid + '\'' +
                '}';
    }
}

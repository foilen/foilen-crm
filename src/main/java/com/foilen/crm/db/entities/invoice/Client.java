package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;

/**
 * A client.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class Client {

    @Id
    private String id;

    private String name;
    private String shortName;

    private String contactName;
    private String email;
    private String address;
    private String tel;
    private String mainSite;

    // FR or EN
    private String lang;

    private String technicalSupportId;

    public Client() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Client other = (Client) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public String getAddress() {
        return address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getLang() {
        return lang;
    }

    public Locale getLangAsLocale() {
        if (lang == null) {
            return null;
        }

        switch (lang) {
            case "EN":
                return Locale.ENGLISH;
            case "FR":
                return Locale.FRENCH;
        }
        return null;
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

    public String getTechnicalSupportId() {
        return technicalSupportId;
    }

    public String getTel() {
        return tel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public Client setAddress(String address) {
        this.address = address;
        return this;
    }

    public Client setContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public Client setEmail(String email) {
        this.email = email;
        return this;
    }

    public Client setId(String id) {
        this.id = id;
        return this;
    }

    public Client setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public Client setMainSite(String mainSite) {
        this.mainSite = mainSite;
        return this;
    }

    public Client setName(String name) {
        this.name = name;
        return this;
    }

    public Client setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public Client setTechnicalSupportId(String technicalSupportId) {
        this.technicalSupportId = technicalSupportId;
        return this;
    }

    public Client setTel(String tel) {
        this.tel = tel;
        return this;
    }

    @Override
    public String toString() {
        return name + " - " + email;
    }

}

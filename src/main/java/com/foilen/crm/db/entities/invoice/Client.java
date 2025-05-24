package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Locale;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * A client.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String shortName;

    @Column(nullable = true)
    private String contactName;

    private String email;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String tel;

    @Column(nullable = true)
    private String mainSite;

    // FR or EN
    @Column(nullable = true)
    private String lang;

    @ManyToOne
    @JoinColumn(name = "technical_support_id", nullable = true)
    private TechnicalSupport technicalSupport;

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

    public Long getId() {
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

    public TechnicalSupport getTechnicalSupport() {
        return technicalSupport;
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

    public Client setId(Long id) {
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

    public Client setTechnicalSupport(TechnicalSupport technicalSupport) {
        this.technicalSupport = technicalSupport;
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

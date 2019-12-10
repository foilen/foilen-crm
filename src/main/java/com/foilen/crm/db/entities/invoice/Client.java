/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.entities.invoice;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A client.
 */
// TODO UI - Client - Edit
// TODO UI - Client - Delete
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Version
    private long version;

    @Column(unique = true, nullable = false)
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

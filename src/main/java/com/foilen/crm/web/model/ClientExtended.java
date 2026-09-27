package com.foilen.crm.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

import java.util.Date;

public class ClientExtended extends AbstractApiBase {

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

    // Transactions and items info
    private Date lastItemDate;
    private Date lastTransactionDate;
    private long currentBalanceInCents;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCurrentBalanceFormatted() {
        return PriceFormatTools.toDigit(currentBalanceInCents);
    }

    public long getCurrentBalanceInCents() {
        return currentBalanceInCents;
    }

    public void setCurrentBalanceInCents(long currentBalanceInCents) {
        this.currentBalanceInCents = currentBalanceInCents;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @JsonIgnore
    public Date getLastItemDate() {
        return lastItemDate;
    }

    public void setLastItemDate(Date lastItemDate) {
        this.lastItemDate = lastItemDate;
    }

    public String getLastItemDateFormatted() {
        return lastItemDate == null ? null : DateTools.formatDateOnly(lastItemDate);
    }

    @JsonIgnore
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public String getLastTransactionDateFormatted() {
        return lastTransactionDate == null ? null : DateTools.formatDateOnly(lastTransactionDate);
    }

    public String getMainSite() {
        return mainSite;
    }

    public void setMainSite(String mainSite) {
        this.mainSite = mainSite;
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

    public TechnicalSupport getTechnicalSupport() {
        return technicalSupport;
    }

    public void setTechnicalSupport(TechnicalSupport technicalSupport) {
        this.technicalSupport = technicalSupport;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}

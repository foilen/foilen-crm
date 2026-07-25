package com.foilen.crm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmConfig {

    // UI
    private String baseUrl;

    // MongoDB
    private String mongoUri = "mongodb://127.0.0.1:27017";
    private String mongoDatabase = "foilen_crm";

    // Email server
    private String mailHost = "127.0.0.1";
    private int mailPort = 25;
    private boolean mailStartTlsEnable = false;
    @Nullable
    private String mailUsername;
    @Nullable
    private String mailPassword;

    // Email that sends
    private String mailFrom;

    // When set, all outgoing emails are redirected to this address instead of their real recipient
    @Nullable
    private String mailForceEmailTo;

    private String company;

    private LoginAzureConfig loginAzureConfig = new LoginAzureConfig();
    private String loginCookieSignatureSalt;

    // Template
    @Nullable
    private String emailTemplateDirectory;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getCompany() {
        return company;
    }

    public String getEmailTemplateDirectory() {
        return emailTemplateDirectory;
    }

    public String getLoginCookieSignatureSalt() {
        return loginCookieSignatureSalt;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public String getMailForceEmailTo() {
        return mailForceEmailTo;
    }

    public String getMailHost() {
        return mailHost;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public int getMailPort() {
        return mailPort;
    }

    public boolean isMailStartTlsEnable() {
        return mailStartTlsEnable;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setEmailTemplateDirectory(String emailTemplateDirectory) {
        this.emailTemplateDirectory = emailTemplateDirectory;
    }

    public void setLoginCookieSignatureSalt(String loginCookieSignatureSalt) {
        this.loginCookieSignatureSalt = loginCookieSignatureSalt;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void setMailForceEmailTo(String mailForceEmailTo) {
        this.mailForceEmailTo = mailForceEmailTo;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public void setMailStartTlsEnable(boolean mailStartTlsEnable) {
        this.mailStartTlsEnable = mailStartTlsEnable;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public void setMongoDatabase(String mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

    public LoginAzureConfig getLoginAzureConfig() {
        return loginAzureConfig;
    }

    public void setLoginAzureConfig(LoginAzureConfig loginAzureConfig) {
        this.loginAzureConfig = loginAzureConfig;
    }
}

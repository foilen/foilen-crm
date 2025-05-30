package com.foilen.crm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmConfig {

    // UI
    private String baseUrl;

    // MySql
    private String mysqlHostName = "127.0.0.1";
    private int mysqlPort = 3306;
    private String mysqlDatabaseName = "foilen_crm";
    private String mysqlDatabaseUserName;
    private String mysqlDatabasePassword;

    // Email server
    private String mailHost = "127.0.0.1";
    private int mailPort = 25;
    @Nullable
    private String mailUsername;
    @Nullable
    private String mailPassword;

    // Email that sends
    private String mailFrom;

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

    public String getMailHost() {
        return mailHost;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public int getMailPort() {
        return mailPort;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public String getMysqlDatabaseName() {
        return mysqlDatabaseName;
    }

    public String getMysqlDatabasePassword() {
        return mysqlDatabasePassword;
    }

    public String getMysqlDatabaseUserName() {
        return mysqlDatabaseUserName;
    }

    public String getMysqlHostName() {
        return mysqlHostName;
    }

    public int getMysqlPort() {
        return mysqlPort;
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

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public void setMysqlDatabaseName(String mysqlDatabaseName) {
        this.mysqlDatabaseName = mysqlDatabaseName;
    }

    public void setMysqlDatabasePassword(String mysqlDatabasePassword) {
        this.mysqlDatabasePassword = mysqlDatabasePassword;
    }

    public void setMysqlDatabaseUserName(String mysqlDatabaseUserName) {
        this.mysqlDatabaseUserName = mysqlDatabaseUserName;
    }

    public void setMysqlHostName(String mysqlHostName) {
        this.mysqlHostName = mysqlHostName;
    }

    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
    }

    public LoginAzureConfig getLoginAzureConfig() {
        return loginAzureConfig;
    }

    public void setLoginAzureConfig(LoginAzureConfig loginAzureConfig) {
        this.loginAzureConfig = loginAzureConfig;
    }
}

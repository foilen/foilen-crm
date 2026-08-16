package com.foilen.crm.db.entities.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class User {

    @Id
    private String email;

    private boolean admin;
    private boolean disabled;

    private String passwordHash;
    private Date passwordLastChange;

    private String loginCode;
    private Date loginCodeExpiration;
    private Date loginCodeLastGenerated;

    private Date creationDate;
    private Date lastLogin;

    public User() {
    }

    public User(String email, boolean admin) {
        this.email = email;
        this.admin = admin;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getEmail() {
        return email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public Date getLoginCodeExpiration() {
        return loginCodeExpiration;
    }

    public Date getLoginCodeLastGenerated() {
        return loginCodeLastGenerated;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Date getPasswordLastChange() {
        return passwordLastChange;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public User setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public User setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public User setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public User setLoginCode(String loginCode) {
        this.loginCode = loginCode;
        return this;
    }

    public User setLoginCodeExpiration(Date loginCodeExpiration) {
        this.loginCodeExpiration = loginCodeExpiration;
        return this;
    }

    public User setLoginCodeLastGenerated(Date loginCodeLastGenerated) {
        this.loginCodeLastGenerated = loginCodeLastGenerated;
        return this;
    }

    public User setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public User setPasswordLastChange(Date passwordLastChange) {
        this.passwordLastChange = passwordLastChange;
        return this;
    }

}

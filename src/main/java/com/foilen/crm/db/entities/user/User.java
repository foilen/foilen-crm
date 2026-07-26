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

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public void setLoginCodeExpiration(Date loginCodeExpiration) {
        this.loginCodeExpiration = loginCodeExpiration;
    }

    public void setLoginCodeLastGenerated(Date loginCodeLastGenerated) {
        this.loginCodeLastGenerated = loginCodeLastGenerated;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPasswordLastChange(Date passwordLastChange) {
        this.passwordLastChange = passwordLastChange;
    }

}

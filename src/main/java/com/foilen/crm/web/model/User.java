package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.Date;

public class User extends AbstractApiBase {

    private String email;

    private boolean admin;
    private boolean disabled;

    private Date lastLogin;

    public Date getLastLogin() {
        return lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getEmail() {
        return email;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

}

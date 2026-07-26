package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ExportUser extends AbstractApiBase {

    private String email;

    private boolean admin;
    private boolean disabled;

    public String getEmail() {
        return email;
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

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

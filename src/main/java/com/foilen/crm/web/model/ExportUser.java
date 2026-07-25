package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ExportUser extends AbstractApiBase {

    private Long id;
    private String userId;
    private boolean admin;
    private String email;

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}

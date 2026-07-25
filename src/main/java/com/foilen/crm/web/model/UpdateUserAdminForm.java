package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class UpdateUserAdminForm extends AbstractApiBase {

    private boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public UpdateUserAdminForm setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

}

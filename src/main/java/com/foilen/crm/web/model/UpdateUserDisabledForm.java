package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class UpdateUserDisabledForm extends AbstractApiBase {

    private boolean disabled;

    public boolean isDisabled() {
        return disabled;
    }

    public UpdateUserDisabledForm setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

}

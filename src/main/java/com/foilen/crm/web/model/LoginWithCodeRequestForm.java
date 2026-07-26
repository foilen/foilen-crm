package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class LoginWithCodeRequestForm extends AbstractApiBase {

    private String email;

    public String getEmail() {
        return email;
    }

    public LoginWithCodeRequestForm setEmail(String email) {
        this.email = email;
        return this;
    }

}

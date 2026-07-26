package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class LoginWithCodeForm extends AbstractApiBase {

    private String email;
    private String code;

    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public LoginWithCodeForm setCode(String code) {
        this.code = code;
        return this;
    }

    public LoginWithCodeForm setEmail(String email) {
        this.email = email;
        return this;
    }

}

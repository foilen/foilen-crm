/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractSingleResult;

public class ApplicationDetailsResult extends AbstractSingleResult<ApplicationDetails> {

    public ApplicationDetailsResult() {
    }

    public ApplicationDetailsResult(ApplicationDetails item) {
        this.setItem(item);
    }

}

package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractSingleResult;

public class ApplicationDetailsResult extends AbstractSingleResult<ApplicationDetails> {

    public ApplicationDetailsResult() {
    }

    public ApplicationDetailsResult(ApplicationDetails item) {
        this.setItem(item);
    }

}

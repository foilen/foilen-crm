/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class CreateOrUpdateTechnicalSupportForm extends AbstractApiBase {

    private String sid;
    // 1099 for 10.99$
    private long pricePerHour;

    public long getPricePerHour() {
        return pricePerHour;
    }

    public String getPricePerHourFormatted() {
        return PriceFormatTools.toDigit(pricePerHour);
    }

    public String getSid() {
        return sid;
    }

    public CreateOrUpdateTechnicalSupportForm setPricePerHour(long pricePerHour) {
        this.pricePerHour = pricePerHour;
        return this;
    }

    public CreateOrUpdateTechnicalSupportForm setSid(String sid) {
        this.sid = sid;
        return this;
    }
}

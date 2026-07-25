package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class CreateOrUpdateTechnicalSupportForm extends AbstractApiBase {

    private String sid;
    // 1099 for 10.99$
    private long pricePerHourInCents;

    public long getPricePerHourInCents() {
        return pricePerHourInCents;
    }

    public String getPricePerHourFormatted() {
        return PriceFormatTools.toDigit(pricePerHourInCents);
    }

    public String getSid() {
        return sid;
    }

    public CreateOrUpdateTechnicalSupportForm setPricePerHourInCents(long pricePerHourInCents) {
        this.pricePerHourInCents = pricePerHourInCents;
        return this;
    }

    public CreateOrUpdateTechnicalSupportForm setSid(String sid) {
        this.sid = sid;
        return this;
    }
}

/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class TechnicalSupport extends AbstractApiBase {

    private String sid;
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

    public void setPricePerHour(long pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}

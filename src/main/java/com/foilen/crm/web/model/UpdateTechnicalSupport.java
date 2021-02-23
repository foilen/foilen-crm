/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.tools.PriceFormatTools;

public class UpdateTechnicalSupport {

    private long id;
    private String sid;
    private long pricePerHour;

    public long getId() {
        return id;
    }

    public long getPricePerHour() {
        return pricePerHour;
    }

    public String getPricePerHourFormatted() {
        return PriceFormatTools.toDigit(pricePerHour);
    }

    public String getSid() {
        return sid;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPricePerHour(long pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}

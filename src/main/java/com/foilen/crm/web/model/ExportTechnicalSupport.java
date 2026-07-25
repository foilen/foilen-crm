package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class ExportTechnicalSupport extends AbstractApiBase {

    private Long id;
    private String sid;
    private long pricePerHour;

    public Long getId() {
        return id;
    }

    public long getPricePerHour() {
        return pricePerHour;
    }

    public String getSid() {
        return sid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPricePerHour(long pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}

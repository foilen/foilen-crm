package com.foilen.crm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foilen.smalltools.restapi.model.AbstractApiBase;

/**
 * The "pricePerHour" JSON property name is pinned via {@link JsonProperty} so previously exported backup files
 * (which use that name) keep loading, even though the Java-side name was updated to pricePerHourInCents.
 */
public class ExportTechnicalSupport extends AbstractApiBase {

    private String id;
    private String sid;
    @JsonProperty("pricePerHour")
    private long pricePerHourInCents;

    public String getId() {
        return id;
    }

    public long getPricePerHourInCents() {
        return pricePerHourInCents;
    }

    public String getSid() {
        return sid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPricePerHourInCents(long pricePerHourInCents) {
        this.pricePerHourInCents = pricePerHourInCents;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}

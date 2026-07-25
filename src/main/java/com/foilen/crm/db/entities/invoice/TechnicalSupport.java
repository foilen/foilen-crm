package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Technical support contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class TechnicalSupport {

    @Id
    private String id;

    private String sid;
    // 1099 for 10.99$
    private long pricePerHourInCents = 0;

    public TechnicalSupport() {
    }

    public TechnicalSupport(String sid, long pricePerHourInCents) {
        this.sid = sid;
        this.pricePerHourInCents = pricePerHourInCents;
    }

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

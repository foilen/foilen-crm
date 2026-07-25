package com.foilen.crm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.Date;

/**
 * The "price" JSON property name is pinned via {@link JsonProperty} so previously exported backup files
 * (which use that name) keep loading, even though the Java-side name was updated to priceInCents.
 */
public class ExportTransaction extends AbstractApiBase {

    private String id;

    // Reference to the Client by its shortName
    private String clientShortName;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    @JsonProperty("price")
    private long priceInCents;

    public String getClientShortName() {
        return clientShortName;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public void setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

}

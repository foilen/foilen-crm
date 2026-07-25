package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

import java.util.Date;

public class Transaction extends AbstractApiBase {

    private String id;

    private ClientShort client;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long priceInCents = 0;

    public String getId() {
        return id;
    }

    public ClientShort getClient() {
        return client;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormatted() {
        return DateTools.formatDateOnly(date);
    }

    public String getDescription() {
        return description;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(priceInCents);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClient(ClientShort client) {
        this.client = client;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

}

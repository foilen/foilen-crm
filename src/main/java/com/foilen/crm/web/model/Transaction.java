package com.foilen.crm.web.model;

import java.util.Date;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

public class Transaction extends AbstractApiBase {

    private ClientShort client;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long price = 0;

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

    public long getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
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

    public void setPrice(long price) {
        this.price = price;
    }

}

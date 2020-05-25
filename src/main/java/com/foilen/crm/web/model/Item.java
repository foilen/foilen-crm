/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import java.util.Date;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

public class Item extends AbstractApiBase {

    private ClientShort client;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long price = 0;

    private String category;

    public String getCategory() {
        return category;
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

    public long getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
    }

    public void setCategory(String category) {
        this.category = category;
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

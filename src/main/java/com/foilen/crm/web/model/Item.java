package com.foilen.crm.web.model;

import java.util.Date;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

public class Item extends AbstractApiBase {

    private Long id;

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

    public Long getId() {
        return id;
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

    public Item setCategory(String category) {
        this.category = category;
        return this;
    }

    public Item setClient(ClientShort client) {
        this.client = client;
        return this;
    }

    public Item setDate(Date date) {
        this.date = date;
        return this;
    }

    public Item setDescription(String description) {
        this.description = description;
        return this;
    }

    public Item setId(Long id) {
        this.id = id;
        return this;
    }

    public Item setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public Item setPrice(long price) {
        this.price = price;
        return this;
    }

}

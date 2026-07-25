package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.Date;

public class ExportItem extends AbstractApiBase {

    private Long id;

    // Reference to the Client by its shortName
    private String clientShortName;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long price;

    private String category;

    public String getCategory() {
        return category;
    }

    public String getClientShortName() {
        return clientShortName;
    }

    public Date getDate() {
        return date;
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

    public void setCategory(String category) {
        this.category = category;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}

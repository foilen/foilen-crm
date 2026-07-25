package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.PriceFormatTools;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Billed and not yet billed items.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class Item {

    @Id
    private String id;

    private String clientId;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long priceInCents = 0;

    private String category;

    public Item() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Item(String clientId, String invoiceId, Date date, String description, long priceInCents, String category) {
        this.clientId = clientId;
        this.invoiceId = invoiceId;
        this.date = date;
        this.description = description;
        this.priceInCents = priceInCents;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getClientId() {
        return clientId;
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

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(priceInCents);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

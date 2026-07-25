package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.PriceFormatTools;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Entries for sent invoices and for cash-in.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class Transaction {

    @Id
    private String id;

    private String clientId;

    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long priceInCents = 0;

    public Transaction() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Transaction(String clientId, String invoiceId, Date date, String description, long priceInCents) {
        this.clientId = clientId;
        this.invoiceId = invoiceId;
        this.date = date;
        this.description = description;
        this.priceInCents = priceInCents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(priceInCents);
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

}

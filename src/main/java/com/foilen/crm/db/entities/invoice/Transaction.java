package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.PriceFormatTools;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Entries for sent invoices and for cash-in.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client;

    @Column(unique = true, nullable = true)
    private String invoiceId;

    private Date date;
    private String description;

    // 1099 for 10.99$
    private long price = 0;

    public Transaction() {
    }

    public Transaction(Client client, String invoiceId, Date date, String description, long price) {
        this.client = client;
        this.invoiceId = invoiceId;
        this.date = date;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public long getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}

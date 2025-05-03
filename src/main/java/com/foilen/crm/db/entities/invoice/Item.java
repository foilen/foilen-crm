package com.foilen.crm.db.entities.invoice;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.PriceFormatTools;

/**
 * Billed and not yet billed items.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Version
    private long version;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @Column(nullable = true)
    private String invoiceId;

    private Date date;
    @Column(length = 2000)
    private String description;

    // 1099 for 10.99$
    private long price = 0;

    @Column(nullable = true)
    private String category;

    public Item() {
    }

    public Item(Client client, String invoiceId, Date date, String description, long price, String category) {
        this.client = client;
        this.invoiceId = invoiceId;
        this.date = date;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public Client getClient() {
        return client;
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

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClient(Client client) {
        this.client = client;
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

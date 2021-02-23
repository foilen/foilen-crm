/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.entities.invoice;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 * An Item to bill recurrently.
 */
// TODO UI - RecurrentItem - Create
// TODO UI - RecurrentItem - Edit
// TODO UI - RecurrentItem - Delete
@Entity
public class RecurrentItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Version
    private long version;

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private Date nextGenerationDate;

    // Item details
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client;

    @Column(length = 2000)
    private String description;

    private long price = 0;// 1099 for 10.99$

    @Column(nullable = true)
    private String category;

    public RecurrentItem() {
    }

    public RecurrentItem(Client client, String description, long price, String category, int calendarUnit, int delta, Date nextGenerationDate) {
        this.client = client;
        this.description = description;
        this.price = price;
        this.category = category;
        this.calendarUnit = calendarUnit;
        this.delta = delta;
        this.nextGenerationDate = nextGenerationDate;
    }

    public int getCalendarUnit() {
        return calendarUnit;
    }

    public String getCategory() {
        return category;
    }

    public Client getClient() {
        return client;
    }

    public int getDelta() {
        return delta;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public Date getNextGenerationDate() {
        return nextGenerationDate;
    }

    public long getPrice() {
        return price;
    }

    public void setCalendarUnit(int calendarUnit) {
        this.calendarUnit = calendarUnit;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNextGenerationDate(Date nextGenerationDate) {
        this.nextGenerationDate = nextGenerationDate;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RecurrentItem [client=");
        builder.append(client);
        builder.append(", description=");
        builder.append(description);
        builder.append(", price=");
        builder.append(price);
        builder.append(", category=");
        builder.append(category);
        builder.append("]");
        return builder.toString();
    }

}

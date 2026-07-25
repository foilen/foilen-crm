package com.foilen.crm.db.entities.invoice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * An Item to bill recurrently.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class RecurrentItem {

    @Id
    private String id;

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private Date nextGenerationDate;

    // Item details
    private String clientId;

    private String description;

    private long priceInCents = 0;// 1099 for 10.99$

    private String category;

    public RecurrentItem() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public RecurrentItem(String clientId, String description, long priceInCents, String category, int calendarUnit, int delta, Date nextGenerationDate) {
        this.clientId = clientId;
        this.description = description;
        this.priceInCents = priceInCents;
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

    public String getClientId() {
        return clientId;
    }

    public int getDelta() {
        return delta;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Date getNextGenerationDate() {
        return nextGenerationDate;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public void setCalendarUnit(int calendarUnit) {
        this.calendarUnit = calendarUnit;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNextGenerationDate(Date nextGenerationDate) {
        this.nextGenerationDate = nextGenerationDate;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RecurrentItem [clientId=");
        builder.append(clientId);
        builder.append(", description=");
        builder.append(description);
        builder.append(", priceInCents=");
        builder.append(priceInCents);
        builder.append(", category=");
        builder.append(category);
        builder.append("]");
        return builder.toString();
    }

}

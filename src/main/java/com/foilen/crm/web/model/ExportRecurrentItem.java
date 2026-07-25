package com.foilen.crm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.Date;

/**
 * The "price" JSON property name is pinned via {@link JsonProperty} so previously exported backup files
 * (which use that name) keep loading, even though the Java-side name was updated to priceInCents.
 */
public class ExportRecurrentItem extends AbstractApiBase {

    private String id;

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private Date nextGenerationDate;

    // Item details
    // Reference to the Client by its shortName
    private String clientShortName;
    private String description;
    @JsonProperty("price")
    private long priceInCents; // 1099 for 10.99$
    private String category;

    public int getCalendarUnit() {
        return calendarUnit;
    }

    public String getCategory() {
        return category;
    }

    public String getClientShortName() {
        return clientShortName;
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

    public void setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
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

}

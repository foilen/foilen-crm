package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

import java.util.Date;

public class ExportRecurrentItem extends AbstractApiBase {

    private Long id;

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private Date nextGenerationDate;

    // Item details
    // Reference to the Client by its shortName
    private String clientShortName;
    private String description;
    private long price; // 1099 for 10.99$
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

    public void setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
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

}

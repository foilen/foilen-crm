/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import java.util.Calendar;
import java.util.Date;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.PriceFormatTools;

public class RecurrentItem extends AbstractApiBase {

    private Long id;

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private Date nextGenerationDate;

    // Item details
    private ClientShort client;

    private String description;

    // 1099 for 10.99$
    private long price = 0;

    private String category;

    public int getCalendarUnit() {
        return calendarUnit;
    }

    public String getCalendarUnitCode() {
        switch (calendarUnit) {
        case Calendar.YEAR:
            return "recurrence.yearly";
        case Calendar.MONTH:
            return "recurrence.monthly";
        default:
            return "recurrence.unknown";
        }
    }

    public String getCategory() {
        return category;
    }

    public ClientShort getClient() {
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

    public String getNextGenerationDateFormatted() {
        return DateTools.formatDateOnly(nextGenerationDate);
    }

    public long getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
    }

    public void setCalendarUnit(int calendarUnit) {
        this.calendarUnit = calendarUnit;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClient(ClientShort client) {
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

}

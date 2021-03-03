/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class CreateOrUpdateRecurrentItemForm extends AbstractApiBase {

    // Recurrence details
    private int calendarUnit; // the unit of the delta that is a constant on {@link Calendar}
    private int delta;

    // Next time
    private String nextGenerationDate;

    // Item details
    private String clientShortName;
    private String description;
    private long price = 0; // 1099 for 10.99$
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

    public String getNextGenerationDate() {
        return nextGenerationDate;
    }

    public long getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(price);
    }

    public CreateOrUpdateRecurrentItemForm setCalendarUnit(int calendarUnit) {
        this.calendarUnit = calendarUnit;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setCategory(String category) {
        this.category = category;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setDelta(int delta) {
        this.delta = delta;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setDescription(String description) {
        this.description = description;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setNextGenerationDate(String nextGenerationDate) {
        this.nextGenerationDate = nextGenerationDate;
        return this;
    }

    public CreateOrUpdateRecurrentItemForm setPrice(long price) {
        this.price = price;
        return this;
    }
}

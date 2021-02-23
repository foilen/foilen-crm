/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import com.foilen.smalltools.tools.PriceFormatTools;

public class UpdateRecurrentItem {

    private long id;
    private int delta;
    private String frequency;
    private String nextGenerationDate;
    private String clientShortName;
    private String description;
    private long price = 0;
    private String category;

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

    public String getFrequency() {
        return frequency;
    }

    public long getId() {
        return id;
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

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNextGenerationDate(String nextGenerationDate) {
        this.nextGenerationDate = nextGenerationDate;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class CreateOrUpdateItem extends AbstractApiBase {

    private String clientShortName;

    private String date;
    private String description;

    // 1099 for 10.99$
    private long priceInCents = 0;

    private String category;

    public String getCategory() {
        return category;
    }

    public String getClientShortName() {
        return clientShortName;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public String getPriceFormatted() {
        return PriceFormatTools.toDigit(priceInCents);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

}

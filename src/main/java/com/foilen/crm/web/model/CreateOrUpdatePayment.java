package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class CreateOrUpdatePayment extends AbstractApiBase {

    private String clientShortName;

    private String date;
    private String paymentType;

    // 1099 for 10.99$
    private long priceInCents = 0;

    public String getClientShortName() {
        return clientShortName;
    }

    public String getDate() {
        return date;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public CreateOrUpdatePayment setClientShortName(String clientShortName) {
        this.clientShortName = clientShortName;
        return this;
    }

    public CreateOrUpdatePayment setDate(String date) {
        this.date = date;
        return this;
    }

    public CreateOrUpdatePayment setPaymentType(String paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public CreateOrUpdatePayment setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
        return this;
    }

}

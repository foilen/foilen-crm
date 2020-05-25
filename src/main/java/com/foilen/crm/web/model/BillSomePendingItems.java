/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import java.util.ArrayList;
import java.util.List;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class BillSomePendingItems extends AbstractApiBase {

    private String invoicePrefix;
    private List<Long> itemToBillIds = new ArrayList<>();

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public List<Long> getItemToBillIds() {
        return itemToBillIds;
    }

    public BillSomePendingItems setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
        return this;
    }

    public BillSomePendingItems setItemToBillIds(List<Long> itemToBillIds) {
        this.itemToBillIds = itemToBillIds;
        return this;
    }

}

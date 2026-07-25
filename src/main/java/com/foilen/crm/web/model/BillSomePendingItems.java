package com.foilen.crm.web.model;

import java.util.ArrayList;
import java.util.List;

import com.foilen.smalltools.restapi.model.AbstractApiBase;

public class BillSomePendingItems extends AbstractApiBase {

    private String invoicePrefix;
    private List<String> itemToBillIds = new ArrayList<>();

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public List<String> getItemToBillIds() {
        return itemToBillIds;
    }

    public BillSomePendingItems setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
        return this;
    }

    public BillSomePendingItems setItemToBillIds(List<String> itemToBillIds) {
        this.itemToBillIds = itemToBillIds;
        return this;
    }

}

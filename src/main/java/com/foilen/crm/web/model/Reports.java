/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.model;

import java.util.ArrayList;
import java.util.List;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class Reports extends AbstractApiBase {

    private List<ReportItemsByCategory> itemsByCategory = new ArrayList<>();
    private List<ReportBalanceByClient> balanceByClient = new ArrayList<>();

    private long globalBalance;

    public List<ReportBalanceByClient> getBalanceByClient() {
        return balanceByClient;
    }

    public long getGlobalBalance() {
        return globalBalance;
    }

    public String getGlobalBalanceFormatted() {
        return PriceFormatTools.toDigit(globalBalance);
    }

    public List<ReportItemsByCategory> getItemsByCategory() {
        return itemsByCategory;
    }

    public void setBalanceByClient(List<ReportBalanceByClient> balanceByClient) {
        this.balanceByClient = balanceByClient;
    }

    public void setGlobalBalance(long globalBalance) {
        this.globalBalance = globalBalance;
    }

    public void setItemsByCategory(List<ReportItemsByCategory> itemsByCategory) {
        this.itemsByCategory = itemsByCategory;
    }

}

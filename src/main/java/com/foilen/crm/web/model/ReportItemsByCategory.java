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

public class ReportItemsByCategory extends AbstractApiBase {

    private String monthDate;
    private String category;
    private long total;

    public ReportItemsByCategory() {
    }

    public ReportItemsByCategory(String monthDate, String category, long total) {
        this.monthDate = monthDate;
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public String getMonthDate() {
        return monthDate;
    }

    public long getTotal() {
        return total;
    }

    public String getTotalFormatted() {
        return PriceFormatTools.toDigit(total);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMonthDate(String monthDate) {
        this.monthDate = monthDate;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}

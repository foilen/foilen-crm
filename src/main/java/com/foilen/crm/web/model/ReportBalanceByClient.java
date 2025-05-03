package com.foilen.crm.web.model;

import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class ReportBalanceByClient extends AbstractApiBase {

    private String clientName;
    private long total;

    public ReportBalanceByClient() {
    }

    public ReportBalanceByClient(String clientName, long total) {
        this.setClientName(clientName);
        this.total = total;
    }

    public String getClientName() {
        return clientName;
    }

    public long getTotal() {
        return total;
    }

    public String getTotalFormatted() {
        return PriceFormatTools.toDigit(total);
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}

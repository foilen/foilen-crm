package com.foilen.crm.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foilen.smalltools.restapi.model.AbstractApiBase;
import com.foilen.smalltools.tools.PriceFormatTools;

public class ReportBalanceByClient extends AbstractApiBase {

    private String clientId;
    private String clientName;
    private long total;
    private long pendingTotal;

    public ReportBalanceByClient() {
    }

    public ReportBalanceByClient(String clientName, long total) {
        this.setClientName(clientName);
        this.total = total;
    }

    @JsonIgnore
    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public long getCurrentBalance() {
        return total + pendingTotal;
    }

    public String getCurrentBalanceFormatted() {
        return PriceFormatTools.toDigit(getCurrentBalance());
    }

    public long getPendingTotal() {
        return pendingTotal;
    }

    public long getTotal() {
        return total;
    }

    public String getTotalFormatted() {
        return PriceFormatTools.toDigit(total);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setPendingTotal(long pendingTotal) {
        this.pendingTotal = pendingTotal;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}

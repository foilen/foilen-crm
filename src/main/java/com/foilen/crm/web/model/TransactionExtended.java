package com.foilen.crm.web.model;

import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.smalltools.tools.PriceFormatTools;

public class TransactionExtended extends Transaction {

    private long balanceFormatted;
    private ClientExtended client;

    public String getBalanceFormatted() {
        return PriceFormatTools.toDigit(balanceFormatted);
    }

    public TransactionExtended setBalanceFormatted(long balanceFormatted) {
        this.balanceFormatted = balanceFormatted;
        return this;
    }

    public ClientExtended getClient() {
        return client;
    }

    public TransactionExtended setClient(ClientExtended client) {
        this.client = client;
        return this;
    }

}

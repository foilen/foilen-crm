package com.foilen.crm.web.model;

import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.smalltools.tools.PriceFormatTools;

public class TransactionWithBalance extends Transaction {

    private long balanceFormatted;

    public String getBalanceFormatted() {
        return PriceFormatTools.toDigit(balanceFormatted);
    }

    public TransactionWithBalance setBalanceFormatted(long balanceFormatted) {
        this.balanceFormatted = balanceFormatted;
        return this;
    }

}

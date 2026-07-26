package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.ReportBalanceByClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface TransactionRepositoryCustom {

    List<ReportBalanceByClient> findAllClientBalance();

    /**
     * All transactions, sorted by date desc, client's name asc, invoiceId desc, id asc.
     *
     * @param clientIdFilter when not null, restricts the results to these clientIds
     */
    Page<Transaction> findAllSortedByClientName(Pageable pageable, Collection<String> clientIdFilter);

    long findTotalByClientId(String clientId);

}

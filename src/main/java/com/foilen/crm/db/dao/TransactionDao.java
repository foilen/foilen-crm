package com.foilen.crm.db.dao;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.ReportBalanceByClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {

    @Query("SELECT new com.foilen.crm.web.model.ReportBalanceByClient(t.client.name, SUM(t.price)) "
            + "FROM Transaction t "
            + "GROUP BY t.client.name "
            + "ORDER BY SUM(t.price) DESC, t.client.name")
    List<ReportBalanceByClient> findAllClientBalance();

    Transaction findByInvoiceId(String invoiceId);

    List<Transaction> findFirst5ByClientOrderByDateDesc(@Param("client") Client client);

    @Query("SELECT SUM(t.price) "
            + "FROM Transaction t "
            + "WHERE t.client = :client "
            + "GROUP BY t.client "
    )
    long findTotalByClient(@Param("client") Client client);

}

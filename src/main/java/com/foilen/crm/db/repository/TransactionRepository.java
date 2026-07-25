package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String>, TransactionRepositoryCustom {

    long deleteAllByClientId(String clientId);

    Transaction findByInvoiceId(String invoiceId);

    List<Transaction> findFirst5ByClientIdOrderByDateDesc(String clientId);

}

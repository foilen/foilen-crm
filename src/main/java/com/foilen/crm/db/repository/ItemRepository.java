package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String>, ItemRepositoryCustom {

    List<Item> findAllByInvoiceId(String invoiceId);

    List<Item> findAllByInvoiceIdIsNullAndClientIdOrderByDateAscDescriptionAsc(String clientId);

    Item findByClientIdAndInvoiceIdNullAndDescription(String clientId, String description);

    Page<Item> findAllByInvoiceIdNotNull(Pageable page);

    long deleteAllByClientId(String clientId);

}

package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String>, ItemRepositoryCustom {

    long deleteAllByClientId(String clientId);

    List<Item> findAllByInvoiceId(String invoiceId);

    List<Item> findAllByInvoiceIdIsNullAndClientIdOrderByDateAscDescriptionAsc(String clientId);

    Page<Item> findAllByInvoiceIdNotNull(Pageable page);

    Item findByClientIdAndInvoiceIdNullAndDescription(String clientId, String description);

    Item findFirst1ByClientIdOrderByDateDesc(String clientId);

}

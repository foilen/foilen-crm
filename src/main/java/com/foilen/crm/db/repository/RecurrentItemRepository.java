package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RecurrentItemRepository extends MongoRepository<RecurrentItem, String>, RecurrentItemRepositoryCustom {

    long deleteAllByClientId(String clientId);

    List<RecurrentItem> findAllByClientId(String clientId);

    List<RecurrentItem> findAllByNextGenerationDateLessThanEqual(Date date);

}

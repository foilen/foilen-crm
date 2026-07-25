package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalSupportRepository extends MongoRepository<TechnicalSupport, String>, TechnicalSupportRepositoryCustom {

    TechnicalSupport findBySid(String sid);

}

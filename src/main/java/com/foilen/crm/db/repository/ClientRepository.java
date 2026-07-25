package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends MongoRepository<Client, String>, ClientRepositoryCustom {

    Client findByShortName(String shortName);

    List<Client> findByTechnicalSupportId(String technicalSupportId);

}

package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends MongoRepository<Client, String>, ClientRepositoryCustom {

    List<Client> findAllByEmailIgnoreCase(String email);

    Page<Client> findAllByEmailIgnoreCase(String email, Pageable pageable);

    List<Client> findAllByTechnicalSupportId(String technicalSupportId);

    Client findByShortName(String shortName);

}

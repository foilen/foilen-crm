package com.foilen.crm.db.dao;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDao extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.name LIKE :search OR" +
            " c.shortName LIKE :search OR" +
            " c.email LIKE :search OR" +
            " c.contactName LIKE :search"
    )
    Page<Client> findAllSearch(@Param("search") String search, Pageable pageable);

    Client findByName(String name);

    Client findByShortName(String shortName);

    List<Client> findByTechnicalSupport(TechnicalSupport technicalSupport);

}

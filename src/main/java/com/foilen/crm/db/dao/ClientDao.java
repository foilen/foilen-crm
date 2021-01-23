/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.invoice.Client;

@Repository
public interface ClientDao extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.name LIKE :search OR" + //
            " c.shortName LIKE :search OR" + //
            " c.email LIKE :search OR" + //
            " c.contactName LIKE :search" //
    )
    Page<Client> findAllSearch(@Param("search") String search, Pageable pageable);

    Client findByName(String name);

    Client findByShortName(String shortName);

}

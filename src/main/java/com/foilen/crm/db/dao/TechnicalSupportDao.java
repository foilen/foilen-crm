/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

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

import com.foilen.crm.db.entities.invoice.TechnicalSupport;

@Repository
public interface TechnicalSupportDao extends JpaRepository<TechnicalSupport, Long> {

    @Query("SELECT ts FROM TechnicalSupport ts WHERE ts.sid LIKE :search")
    Page<TechnicalSupport> findAllSearch(@Param("search") String search, Pageable pageable);

    TechnicalSupport findBySid(String sid);

}

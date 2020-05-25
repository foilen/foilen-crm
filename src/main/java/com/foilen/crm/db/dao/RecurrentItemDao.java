/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;

@Repository
public interface RecurrentItemDao extends JpaRepository<RecurrentItem, Long> {

    List<RecurrentItem> findAllByNextGenerationDateLessThanEqual(Date date);

}

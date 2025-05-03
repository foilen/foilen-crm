package com.foilen.crm.db.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;

@Repository
public interface RecurrentItemDao extends JpaRepository<RecurrentItem, Long> {

    List<RecurrentItem> findAllByClientShortName(String clientShortName);

    List<RecurrentItem> findAllByNextGenerationDateLessThanEqual(Date date);

}

package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.TechnicalSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TechnicalSupportRepositoryCustom {

    Page<TechnicalSupport> findAllSearch(String search, Pageable pageable);

}

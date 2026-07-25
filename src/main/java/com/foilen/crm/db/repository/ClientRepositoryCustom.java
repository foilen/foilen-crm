package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepositoryCustom {

    Page<Client> findAllSearch(String search, Pageable pageable);

}

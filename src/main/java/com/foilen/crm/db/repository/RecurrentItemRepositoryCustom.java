package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecurrentItemRepositoryCustom {

    /**
     * All recurrent items, sorted by client's name asc, category desc, description desc, id asc.
     */
    Page<RecurrentItem> findAllSortedByClientName(Pageable pageable);

}

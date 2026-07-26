package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface RecurrentItemRepositoryCustom {

    /**
     * All recurrent items, sorted by client's name asc, category desc, description desc, id asc.
     *
     * @param clientIdFilter when not null, restricts the results to these clientIds
     */
    Page<RecurrentItem> findAllSortedByClientName(Pageable pageable, Collection<String> clientIdFilter);

}

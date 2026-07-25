package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.web.model.ReportItemsByCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    /**
     * Billed items (invoiceId not null), sorted by invoiceId desc, client's name asc, date desc, id asc.
     */
    Page<Item> findAllBilledSortedByClientName(Pageable pageable);

    List<String> findAllDistinctCategories();

    /**
     * Distinct clientIds having at least one pending (not yet invoiced) item.
     */
    List<String> findAllDistinctClientIdByInvoiceIdNull();

    List<ReportItemsByCategory> findAllItemsByCategory();

    /**
     * Pending items (invoiceId null), sorted by client's name asc, date desc, id asc.
     */
    Page<Item> findAllPendingSortedByClientName(Pageable pageable);

}

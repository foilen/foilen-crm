package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.web.model.ReportItemsByCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemRepositoryCustom {

    /**
     * Billed items (invoiceId not null), sorted by invoiceId desc, client's name asc, date desc, id asc.
     *
     * @param clientIdFilter when not null, restricts the results to these clientIds
     */
    Page<Item> findAllBilledSortedByClientName(Pageable pageable, Collection<String> clientIdFilter);

    List<String> findAllDistinctCategories();

    /**
     * Distinct clientIds having at least one pending (not yet invoiced) item.
     */
    List<String> findAllDistinctClientIdByInvoiceIdNull();

    List<ReportItemsByCategory> findAllItemsByCategory();

    /**
     * Pending items (invoiceId null), sorted by client's name asc, date desc, id asc.
     *
     * @param clientIdFilter when not null, restricts the results to these clientIds
     */
    Page<Item> findAllPendingSortedByClientName(Pageable pageable, Collection<String> clientIdFilter);

    /**
     * Sum of priceInCents of the pending (not yet invoiced) items, grouped by clientId.
     */
    Map<String, Long> findAllPendingTotalsByClientId();

    /**
     * Sum of priceInCents of the pending (not yet invoiced) items, grouped by clientId, restricted to the given clients.
     */
    Map<String, Long> findPendingTotalsByClientIds(Collection<String> clientIds);

}

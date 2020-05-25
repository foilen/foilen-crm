/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.db.dao;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.web.model.ReportItemsByCategory;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {

    List<Item> findAllByInvoiceId(String invoiceId);

    @OrderBy("date, description")
    List<Item> findAllByInvoiceIdIsNullAndClient(Client client);

    Page<Item> findAllByInvoiceIdNotNull(Pageable page);

    Page<Item> findAllByInvoiceIdNull(Pageable page);

    @Query("SELECT DISTINCT i.client FROM Item i WHERE i.invoiceId IS NULL ORDER BY i.client.shortName")
    List<Client> findAllClientByInvoiceIdNull();

    @Query("SELECT new com.foilen.crm.web.model.ReportItemsByCategory( CONCAT(YEAR(date), CONCAT('-', MONTH(date))), " //
            + "  category, " //
            + "  sum(price)) " //
            + "FROM Item " //
            + "GROUP BY CONCAT(YEAR(date), CONCAT('-', MONTH(date))), category " //
            + "ORDER BY CONCAT(YEAR(date), CONCAT('-', MONTH(date))) DESC, category")
    List<ReportItemsByCategory> findAllItemsByCategory();

    Item findByClientAndInvoiceIdNullAndDescription(Client client, String description);

}

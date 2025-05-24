package com.foilen.crm.db.dao;

import com.foilen.crm.db.entities.invoice.Client;
import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.web.model.ReportItemsByCategory;
import jakarta.persistence.OrderBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {

    List<Item> findAllByInvoiceId(String invoiceId);

    @OrderBy("date, description")
    List<Item> findAllByInvoiceIdIsNullAndClient(Client client);

    Page<Item> findAllByInvoiceIdNotNull(Pageable page);

    Page<Item> findAllByInvoiceIdNull(Pageable page);

    @Query("SELECT DISTINCT i.client FROM Item i WHERE i.invoiceId IS NULL ORDER BY i.client.shortName")
    List<Client> findAllClientByInvoiceIdNull();

    @Query("SELECT new com.foilen.crm.web.model.ReportItemsByCategory( CONCAT(YEAR(date), CONCAT('-', MONTH(date))), "
            + "  category, "
            + "  sum(price)) "
            + "FROM Item "
            + "GROUP BY DATE_FORMAT(date,'%Y-%m'), category "
            + "ORDER BY DATE_FORMAT(date,'%Y-%m') DESC, category")
    List<ReportItemsByCategory> findAllItemsByCategory();

    Item findByClientAndInvoiceIdNullAndDescription(Client client, String description);

}

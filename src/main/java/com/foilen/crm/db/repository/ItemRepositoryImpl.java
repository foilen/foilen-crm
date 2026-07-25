package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Item;
import com.foilen.crm.web.model.ReportItemsByCategory;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ItemRepositoryImpl extends AbstractRepositoryCustom implements ItemRepositoryCustom {

    @Override
    public List<String> findAllDistinctClientIdByInvoiceIdNull() {
        Query query = new Query(Criteria.where("invoiceId").is(null));
        return mongoOperations.query(Item.class)
                .distinct("clientId")
                .matching(query)
                .as(String.class)
                .all();
    }

    @Override
    public List<String> findAllDistinctCategories() {
        List<String> categories = new ArrayList<>(mongoOperations.query(Item.class)
                .distinct("category")
                .as(String.class)
                .all());
        categories.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        return categories;
    }

    @Override
    public List<ReportItemsByCategory> findAllItemsByCategory() {

        AggregationOperation groupStage = context -> new Document("$group", new Document("_id",
                new Document("year", new Document("$year", "$date"))
                        .append("month", new Document("$month", "$date"))
                        .append("category", "$category"))
                .append("total", new Document("$sum", "$priceInCents")));

        AggregationOperation projectStage = context -> new Document("$project", new Document("_id", 0)
                .append("year", "$_id.year")
                .append("month", "$_id.month")
                .append("category", "$_id.category")
                .append("total", 1)
                .append("monthDate", new Document("$concat", List.of(
                        new Document("$toString", "$_id.year"),
                        "-",
                        new Document("$toString", "$_id.month")
                ))));

        AggregationOperation sortStage = context -> new Document("$sort", new Document("year", -1).append("month", -1).append("category", 1));

        return aggregation(Item.class, ReportItemsByCategory.class, List.of(groupStage, projectStage, sortStage));
    }

    @Override
    public Page<Item> findAllBilledSortedByClientName(Pageable pageable) {
        return aggregation(Item.class, Item.class, pageable, billedOrPendingPipeline(true));
    }

    @Override
    public Page<Item> findAllPendingSortedByClientName(Pageable pageable) {
        return aggregation(Item.class, Item.class, pageable, billedOrPendingPipeline(false));
    }

    private List<AggregationOperation> billedOrPendingPipeline(boolean billed) {

        AggregationOperation matchStage = billed
                ? Aggregation.match(Criteria.where("invoiceId").ne(null))
                : Aggregation.match(Criteria.where("invoiceId").is(null));

        AggregationOperation lookupStage = lookupByStringId("client", "clientId", "clientLookup");

        AggregationOperation unwindStage = context -> new Document("$unwind", new Document("path", "$clientLookup").append("preserveNullAndEmptyArrays", true));

        AggregationOperation addFieldsStage = context -> new Document("$addFields", new Document("clientName", "$clientLookup.name"));

        AggregationOperation sortStage = billed
                ? (context -> new Document("$sort", new Document("invoiceId", -1).append("clientName", 1).append("date", -1).append("_id", 1)))
                : (context -> new Document("$sort", new Document("clientName", 1).append("date", -1).append("_id", 1)));

        return List.of(matchStage, lookupStage, unwindStage, addFieldsStage, sortStage);
    }

}

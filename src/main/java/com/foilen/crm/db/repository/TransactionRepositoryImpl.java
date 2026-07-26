package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.Transaction;
import com.foilen.crm.web.model.ReportBalanceByClient;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class TransactionRepositoryImpl extends AbstractRepositoryCustom implements TransactionRepositoryCustom {

    @Override
    public List<ReportBalanceByClient> findAllClientBalance() {

        AggregationOperation groupStage = context -> new Document("$group", new Document("_id", "$clientId")
                .append("total", new Document("$sum", "$priceInCents")));

        AggregationOperation lookupStage = lookupByStringId("client", "_id", "clientLookup");

        AggregationOperation unwindStage = context -> new Document("$unwind", new Document("path", "$clientLookup").append("preserveNullAndEmptyArrays", true));

        AggregationOperation projectStage = context -> new Document("$project", new Document("_id", 0)
                .append("clientId", "$_id")
                .append("clientName", "$clientLookup.name")
                .append("total", 1));

        AggregationOperation sortStage = context -> new Document("$sort", new Document("total", -1).append("clientName", 1));

        return aggregation(Transaction.class, ReportBalanceByClient.class, List.of(groupStage, lookupStage, unwindStage, projectStage, sortStage));
    }

    @Override
    public Page<Transaction> findAllSortedByClientName(Pageable pageable, Collection<String> clientIdFilter) {

        AggregationOperation lookupStage = lookupByStringId("client", "clientId", "clientLookup");

        AggregationOperation unwindStage = context -> new Document("$unwind", new Document("path", "$clientLookup").append("preserveNullAndEmptyArrays", true));

        AggregationOperation addFieldsStage = context -> new Document("$addFields", new Document("clientName", "$clientLookup.name"));

        AggregationOperation sortStage = context -> new Document("$sort", new Document("date", -1).append("clientName", 1).append("invoiceId", -1).append("_id", 1));

        List<AggregationOperation> pipeline = new ArrayList<>();
        if (clientIdFilter != null) {
            pipeline.add(Aggregation.match(Criteria.where("clientId").in(clientIdFilter)));
        }
        pipeline.add(lookupStage);
        pipeline.add(unwindStage);
        pipeline.add(addFieldsStage);
        pipeline.add(sortStage);

        return aggregation(Transaction.class, Transaction.class, pageable, pipeline);
    }

    @Override
    public long findTotalByClientId(String clientId) {

        AggregationOperation matchStage = Aggregation.match(Criteria.where("clientId").is(clientId));
        AggregationOperation groupStage = context -> new Document("$group", new Document("_id", "$clientId")
                .append("total", new Document("$sum", "$priceInCents")));

        List<Document> results = aggregation(Transaction.class, Document.class, List.of(matchStage, groupStage));
        if (results.isEmpty()) {
            return 0;
        }
        Number total = (Number) results.getFirst().get("total");
        return total.longValue();
    }

}

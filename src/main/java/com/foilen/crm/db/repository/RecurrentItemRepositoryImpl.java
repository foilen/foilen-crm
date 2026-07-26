package com.foilen.crm.db.repository;

import com.foilen.crm.db.entities.invoice.RecurrentItem;
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
public class RecurrentItemRepositoryImpl extends AbstractRepositoryCustom implements RecurrentItemRepositoryCustom {

    @Override
    public Page<RecurrentItem> findAllSortedByClientName(Pageable pageable, Collection<String> clientIdFilter) {

        AggregationOperation lookupStage = lookupByStringId("client", "clientId", "clientLookup");

        AggregationOperation unwindStage = context -> new Document("$unwind", new Document("path", "$clientLookup").append("preserveNullAndEmptyArrays", true));

        AggregationOperation addFieldsStage = context -> new Document("$addFields", new Document("clientName", "$clientLookup.name"));

        AggregationOperation sortStage = context -> new Document("$sort", new Document("clientName", 1).append("category", -1).append("description", -1).append("_id", 1));

        List<AggregationOperation> pipeline = new ArrayList<>();
        if (clientIdFilter != null) {
            pipeline.add(Aggregation.match(Criteria.where("clientId").in(clientIdFilter)));
        }
        pipeline.add(lookupStage);
        pipeline.add(unwindStage);
        pipeline.add(addFieldsStage);
        pipeline.add(sortStage);

        return aggregation(RecurrentItem.class, RecurrentItem.class, pageable, pipeline);
    }

}

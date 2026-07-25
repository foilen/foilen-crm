package com.foilen.crm.db.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class AbstractRepositoryCustom {

    @Autowired
    protected MongoOperations mongoOperations;

    protected <T> List<T> aggregation(Class<?> inputType, Class<T> outputType, List<AggregationOperation> aggregationOperations) {
        return mongoOperations.aggregate(Aggregation.newAggregation(aggregationOperations), inputType, outputType).getMappedResults();
    }

    protected <T> Page<T> aggregation(Class<?> inputType, Class<T> outputType, Pageable pageable, List<AggregationOperation> aggregationOperations) {
        List<AggregationOperation> aggregationOperationsItems = new ArrayList<>(aggregationOperations);
        if (pageable.getSort().isSorted()) {
            aggregationOperationsItems.add(Aggregation.sort(pageable.getSort()));
        }
        aggregationOperationsItems.add(Aggregation.skip(pageable.getOffset()));
        aggregationOperationsItems.add(Aggregation.limit(pageable.getPageSize()));
        List<T> items = mongoOperations.aggregate(Aggregation.newAggregation(aggregationOperationsItems), inputType, outputType).getMappedResults();

        List<AggregationOperation> aggregationOperationsCount = new ArrayList<>(aggregationOperations);
        aggregationOperationsCount.add(Aggregation.count().as("total"));
        List<Document> countResults = mongoOperations.aggregate(Aggregation.newAggregation(aggregationOperationsCount), inputType, Document.class).getMappedResults();
        long total = 0;
        if (!countResults.isEmpty()) {
            total = countResults.getFirst().getInteger("total");
        }
        return new PageImpl<>(items, pageable, total);
    }

    protected <T> Page<T> find(Class<T> entityClass, Pageable pageable, Criteria... criterias) {
        return find(entityClass, pageable, List.of(criterias));
    }

    protected <T> Page<T> find(Class<T> entityClass, Pageable pageable, List<Criteria> criterias) {
        Query query = new Query();
        if (!criterias.isEmpty()) {
            query.addCriteria(criterias.size() == 1 ? criterias.getFirst() : new Criteria().andOperator(criterias));
        }
        long total = mongoOperations.count(query, entityClass);
        query.with(pageable);
        List<T> items = mongoOperations.find(query, entityClass);
        return new PageImpl<>(items, pageable, total);
    }

    protected <T> List<T> find(Class<T> entityClass, Sort sort, List<Criteria> criterias) {
        Query query = new Query();
        if (!criterias.isEmpty()) {
            query.addCriteria(criterias.size() == 1 ? criterias.getFirst() : new Criteria().andOperator(criterias));
        }
        query.with(sort);
        return mongoOperations.find(query, entityClass);
    }

    /**
     * A $lookup joining on a plain-String reference field (e.g. "clientId") against another collection's "_id",
     * which Spring Data stores as a real ObjectId. A simple localField/foreignField $lookup would silently match
     * nothing since the BSON types differ, so this compares them both as strings via the pipeline form.
     */
    protected AggregationOperation lookupByStringId(String fromCollection, String localField, String asField) {
        return context -> new Document("$lookup", new Document("from", fromCollection)
                .append("let", new Document("localId", "$" + localField))
                .append("pipeline", List.of(
                        new Document("$match", new Document("$expr", new Document("$eq", List.of(
                                new Document("$toString", "$_id"),
                                "$$localId"
                        ))))
                ))
                .append("as", asField));
    }

}

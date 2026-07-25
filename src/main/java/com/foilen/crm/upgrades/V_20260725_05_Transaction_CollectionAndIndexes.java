package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.model.IndexOptions;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_05_Transaction_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("transaction");
        addIndex("transaction",
                new IndexOptions().unique(true).sparse(true),
                new Tuple2<>("invoiceId", 1)
        );
        addIndex("transaction", new Tuple2<>("clientId", 1));
    }

}

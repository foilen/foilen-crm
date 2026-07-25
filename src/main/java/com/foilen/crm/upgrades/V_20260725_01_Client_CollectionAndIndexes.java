package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.model.IndexOptions;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_01_Client_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("client");
        addIndex("client",
                new IndexOptions().unique(true),
                new Tuple2<>("shortName", 1)
        );
    }

}

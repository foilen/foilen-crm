package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.model.IndexOptions;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_06_User_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("user");
        addIndex("user",
                new IndexOptions().unique(true),
                new Tuple2<>("userId", 1)
        );
    }

}

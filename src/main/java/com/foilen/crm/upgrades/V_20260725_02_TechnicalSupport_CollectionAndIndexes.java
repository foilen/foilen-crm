package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.model.IndexOptions;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_02_TechnicalSupport_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("technicalSupport");
        addIndex("technicalSupport",
                new IndexOptions().unique(true),
                new Tuple2<>("sid", 1)
        );
    }

}

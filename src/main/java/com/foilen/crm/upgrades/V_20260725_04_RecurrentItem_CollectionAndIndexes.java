package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_04_RecurrentItem_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("recurrentItem");
        addIndex("recurrentItem", new Tuple2<>("clientId", 1));
        addIndex("recurrentItem", new Tuple2<>("nextGenerationDate", 1));
    }

}

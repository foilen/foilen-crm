package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import org.springframework.stereotype.Component;

@Component
public class V_20260725_03_Item_CollectionAndIndexes extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        addCollection("item");
        addIndex("item", new Tuple2<>("clientId", 1));
        addIndex("item", new Tuple2<>("invoiceId", 1));
        addIndex("item", new Tuple2<>("category", 1));
    }

}

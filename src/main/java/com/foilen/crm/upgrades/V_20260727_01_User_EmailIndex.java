package com.foilen.crm.upgrades;

import com.foilen.smalltools.tuple.Tuple2;
import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.model.IndexOptions;
import org.springframework.stereotype.Component;

@Component
public class V_20260727_01_User_EmailIndex extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {
        dropIndex("user", "userId_1");
    }

}

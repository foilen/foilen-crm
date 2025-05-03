package com.foilen.crm.upgrades;

import org.springframework.stereotype.Component;

import com.foilen.smalltools.upgrader.tasks.AbstractDatabaseUpgradeTask;

@Component
public class V2019121001_Initial extends AbstractDatabaseUpgradeTask {

    @Override
    public void execute() {
        updateFromResource("V2019121001_Initial.sql");
    }

}

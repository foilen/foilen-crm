package com.foilen.crm.upgrades;

import org.springframework.stereotype.Component;

import com.foilen.smalltools.upgrader.tasks.AbstractDatabaseUpgradeTask;

@Component
public class V2021030301_RemoveClientUniqueName extends AbstractDatabaseUpgradeTask {

    @Override
    public void execute() {
        jdbcTemplate.update("alter table client drop index UK_dn5jasds5r1j3ewo5k3nhwkkq");
    }

}

/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.upgrades;

import org.springframework.stereotype.Component;

import com.foilen.smalltools.upgrader.tasks.AbstractDatabaseUpgradeTask;

@Component
public class V2021030201_AddCascadeDelete extends AbstractDatabaseUpgradeTask {

    @Override
    public void execute() {
        updateFromResource("V2021030201_AddCascadeDelete.sql");
    }

}

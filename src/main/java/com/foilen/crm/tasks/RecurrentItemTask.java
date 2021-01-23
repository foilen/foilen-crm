/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.tasks;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.foilen.crm.services.RecurrentItemService;
import com.foilen.smalltools.tools.AbstractBasics;

@Component
public class RecurrentItemTask extends AbstractBasics {

    @Autowired
    private RecurrentItemService recurrentItemService;

    @Scheduled(fixedRate = 60 * 60000, initialDelay = 60000) // Every hour
    public void generateReady() {
        logger.info("Generate those that are ready");

        recurrentItemService.generateReady(new Date());
    }
}

/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.localonly;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class LocalLaunchService {

    private FakeDataService fakeDataService;

    public LocalLaunchService(FakeDataService fakeDataService) {
        this.fakeDataService = fakeDataService;
    }

    @Order(3)
    @EventListener
    public void createTheData(ContextRefreshedEvent event) {
        fakeDataService.createAll();
    }

}

package com.foilen.crm.localonly;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class LocalLaunchService {

    private final FakeDataService fakeDataService;
    private final boolean includeUsers;

    public LocalLaunchService(FakeDataService fakeDataService, boolean includeUsers) {
        this.fakeDataService = fakeDataService;
        this.includeUsers = includeUsers;
    }

    @Order(3)
    @EventListener
    public void createTheData(ContextRefreshedEvent event) {
        if (includeUsers) {
            fakeDataService.createAll();
        } else {
            fakeDataService.createAllExceptUsers();
        }
    }

}

/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foilen.crm.services.ItemService;
import com.foilen.crm.web.model.BillPendingItems;
import com.foilen.crm.web.model.BillSomePendingItems;
import com.foilen.crm.web.model.CreateItem;
import com.foilen.crm.web.model.ItemList;
import com.foilen.smalltools.restapi.model.FormResult;

@RequestMapping(value = "api/item", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @PostMapping("billPending")
    public FormResult billPending(Authentication authentication, //
            @RequestBody BillPendingItems form //
    ) {
        return itemService.billPending(authentication.getName(), form.getInvoicePrefix());
    }

    @PostMapping("billSomePending")
    public FormResult billSomePending(Authentication authentication, //
            @RequestBody BillSomePendingItems form //
    ) {
        return itemService.billSomePending(authentication.getName(), form);
    }

    @PostMapping("")
    public FormResult create(Authentication authentication, //
            @RequestBody CreateItem form //
    ) {
        return itemService.create(authentication.getName(), form);
    }

    @GetMapping("listBilled")
    public ItemList listBilled(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId, //
            @RequestParam(required = false) String search //
    ) {
        return itemService.listBilled(authentication.getName(), pageId);
    }

    @GetMapping("listPending")
    public ItemList listPending(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId, //
            @RequestParam(required = false) String search //
    ) {
        return itemService.listPending(authentication.getName(), pageId);
    }

}

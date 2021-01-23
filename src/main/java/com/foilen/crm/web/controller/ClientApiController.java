/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

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

import com.foilen.crm.services.ClientService;
import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateClient;
import com.foilen.smalltools.restapi.model.FormResult;

@RequestMapping(value = "api/client", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class ClientApiController {

    @Autowired
    private ClientService clientService;

    @PostMapping("")
    public FormResult create(Authentication authentication, //
            @RequestBody CreateClient form //
    ) {
        return clientService.create(authentication.getName(), form);
    }

    @GetMapping("listAll")
    public ClientList listAll(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId, //
            @RequestParam(required = false) String search //
    ) {
        return clientService.listAll(authentication.getName(), pageId, search);
    }

}

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foilen.crm.services.RecurrentItemService;
import com.foilen.crm.web.model.RecurrentItemList;

@RequestMapping(value = "api/recurrentItem", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class RecurrentItemApiController {

    @Autowired
    private RecurrentItemService recurrentItemService;

    @GetMapping("listAll")
    public RecurrentItemList listAll(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId //
    ) {
        return recurrentItemService.listAll(authentication.getName(), pageId);
    }

}

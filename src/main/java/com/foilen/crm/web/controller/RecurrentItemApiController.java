/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.controller;

import com.foilen.crm.web.model.CreateRecurrentItem;
import com.foilen.crm.web.model.UpdateRecurrentItem;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    public FormResult create(Authentication authentication, @RequestBody CreateRecurrentItem form) {
        return recurrentItemService.create(authentication.getName(), form);
    }

    @DeleteMapping("/{id}")
    public FormResult delete(@PathVariable("id") long id) {
        return recurrentItemService.delete(id);
    }

    @PutMapping("/{id}")
    public FormResult update(Authentication authentication, @PathVariable("id") long id, @RequestBody UpdateRecurrentItem form) {
        return recurrentItemService.update(authentication.getName(), id, form);
    }

}

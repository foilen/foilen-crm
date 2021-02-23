/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.controller;

import com.foilen.crm.web.model.CreateTechnicalSupport;
import com.foilen.crm.web.model.UpdateTechnicalSupport;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import com.foilen.crm.services.TechnicalSupportService;
import com.foilen.crm.web.model.TechnicalSupportList;

@RequestMapping(value = "api/technicalSupport", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class TechnicalSupportApiController {

    @Autowired
    private TechnicalSupportService technicalSupportService;

    @GetMapping("listAll")
    public TechnicalSupportList listAll(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId, //
            @RequestParam(required = false) String search //
    ) {
        return technicalSupportService.listAll(authentication.getName(), pageId, search);
    }

    @PostMapping
    public FormResult create(Authentication authentication, @RequestBody CreateTechnicalSupport form) {
        return technicalSupportService.create(authentication.getName(), form);
    }

    @PutMapping("/{sid}")
    public FormResult update(Authentication authentication,
                             @PathVariable("sid") String sid,
                             @RequestBody UpdateTechnicalSupport form
    ) {
        return technicalSupportService.update(authentication.getName(), sid, form);
    }

    @DeleteMapping("/{sid}")
    public FormResult delete(@PathVariable("sid") String sid) {
        return technicalSupportService.delete(sid);
    }
}

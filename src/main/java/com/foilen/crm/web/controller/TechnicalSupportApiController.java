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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}

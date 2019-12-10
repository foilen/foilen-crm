/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

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

import com.foilen.crm.services.TransactionService;
import com.foilen.crm.web.model.TransactionList;

@RequestMapping(value = "api/transaction", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class TransactionApiController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("listAll")
    public TransactionList listAll(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId //
    ) {
        return transactionService.listAll(authentication.getName(), pageId);
    }

}

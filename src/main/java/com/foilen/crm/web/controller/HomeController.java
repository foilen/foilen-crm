/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foilen.smalltools.tools.ResourceTools;

@RequestMapping("/")
@Controller
public class HomeController {

    private static final String INDEX_HTML = ResourceTools.getResourceAsString("/WEB-INF/crm/web/index.html");

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return INDEX_HTML;
    }

}

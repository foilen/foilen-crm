package com.foilen.crm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foilen.crm.services.ApplicationService;
import com.foilen.crm.web.model.ApplicationDetailsResult;

@RequestMapping(value = "api/app", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class AppApiController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("details")
    public ApplicationDetailsResult details(Authentication authentication) {
        return applicationService.getDetails(authentication.getName());
    }

}

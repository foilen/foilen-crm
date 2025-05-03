package com.foilen.crm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foilen.crm.services.TechnicalSupportService;
import com.foilen.crm.web.model.CreateOrUpdateTechnicalSupportForm;
import com.foilen.crm.web.model.TechnicalSupportList;
import com.foilen.smalltools.restapi.model.FormResult;

@RequestMapping(value = "api/technicalSupport", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class TechnicalSupportApiController {

    @Autowired
    private TechnicalSupportService technicalSupportService;

    @PostMapping
    public FormResult create(Authentication authentication, //
            @RequestBody CreateOrUpdateTechnicalSupportForm form //
    ) {
        return technicalSupportService.create(authentication.getName(), form);
    }

    @DeleteMapping("{sid}")
    public FormResult delete(Authentication authentication, //
            @PathVariable("sid") String sid //
    ) {
        return technicalSupportService.delete(authentication.getName(), sid);
    }

    @GetMapping("listAll")
    public TechnicalSupportList listAll(Authentication authentication, //
            @RequestParam(defaultValue = "1") int pageId, //
            @RequestParam(required = false) String search //
    ) {
        return technicalSupportService.listAll(authentication.getName(), pageId, search);
    }

    @PutMapping("{technicalSupportSid}")
    public FormResult update(Authentication authentication, //
            @PathVariable("technicalSupportSid") String technicalSupportSid, //
            @RequestBody CreateOrUpdateTechnicalSupportForm form //
    ) {
        return technicalSupportService.update(authentication.getName(), technicalSupportSid, form);
    }
}

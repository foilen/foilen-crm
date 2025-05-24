package com.foilen.crm.web.controller;

import com.foilen.crm.services.ClientService;
import com.foilen.crm.web.model.ClientList;
import com.foilen.crm.web.model.CreateOrUpdateClientForm;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "api/client", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class ClientApiController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public FormResult create(Authentication authentication,
                             @RequestBody CreateOrUpdateClientForm form
    ) {
        return clientService.create(authentication.getName(), form);
    }

    @DeleteMapping("{clientShortName}")
    public FormResult delete(Authentication authentication,
                             @PathVariable String clientShortName
    ) {
        return clientService.delete(authentication.getName(), clientShortName);
    }

    @GetMapping("listAll")
    public ClientList listAll(Authentication authentication,
                              @RequestParam(defaultValue = "1") int pageId,
                              @RequestParam(required = false) String search
    ) {
        return clientService.listAll(authentication.getName(), pageId, search);
    }

    @PutMapping("{clientShortName}")
    public FormResult update(Authentication authentication,
                             @PathVariable String clientShortName,
                             @RequestBody CreateOrUpdateClientForm form
    ) {
        return clientService.update(authentication.getName(), clientShortName, form);
    }
}

package com.foilen.crm.web.controller;

import com.foilen.crm.services.RecurrentItemService;
import com.foilen.crm.web.model.CreateOrUpdateRecurrentItemForm;
import com.foilen.crm.web.model.RecurrentItemList;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "api/recurrentItem", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class RecurrentItemApiController {

    @Autowired
    private RecurrentItemService recurrentItemService;

    @PostMapping
    public FormResult create(Authentication authentication,
                             @RequestBody CreateOrUpdateRecurrentItemForm form
    ) {
        return recurrentItemService.create(authentication.getName(), form);
    }

    @DeleteMapping("{id}")
    public FormResult delete(Authentication authentication,
                             @PathVariable("id") long id
    ) {
        return recurrentItemService.delete(authentication.getName(), id);
    }

    @GetMapping("listAll")
    public RecurrentItemList listAll(Authentication authentication,
                                     @RequestParam(defaultValue = "1") int pageId
    ) {
        return recurrentItemService.listAll(authentication.getName(), pageId);
    }

    @PutMapping("{id}")
    public FormResult update(Authentication authentication,
                             @PathVariable("id") long id,
                             @RequestBody CreateOrUpdateRecurrentItemForm form
    ) {
        return recurrentItemService.update(authentication.getName(), id, form);
    }

}

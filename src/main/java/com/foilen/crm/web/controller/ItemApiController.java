package com.foilen.crm.web.controller;

import com.foilen.crm.services.ItemService;
import com.foilen.crm.web.model.*;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "api/item", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @PostMapping("billPending")
    public FormResult billPending(Authentication authentication,
                                  @RequestBody BillPendingItems form
    ) {
        return itemService.billPending(authentication.getName(), form.getInvoicePrefix());
    }

    @PostMapping("billSomePending")
    public FormResult billSomePending(Authentication authentication,
                                      @RequestBody BillSomePendingItems form
    ) {
        return itemService.billSomePending(authentication.getName(), form);
    }

    @PostMapping("createWithTime")
    public FormResult create(Authentication authentication,
                             @RequestBody CreateItemWithTime form
    ) {
        return itemService.create(authentication.getName(), form);
    }

    @PostMapping
    public FormResult create(Authentication authentication,
                             @RequestBody CreateOrUpdateItem form
    ) {
        return itemService.create(authentication.getName(), form);
    }

    @DeleteMapping("{id}")
    public FormResult delete(Authentication authentication,
                             @PathVariable("id") long id
    ) {
        return itemService.delete(authentication.getName(), id);
    }

    @GetMapping("listBilled")
    public ItemList listBilled(Authentication authentication,
                               @RequestParam(defaultValue = "1") int pageId,
                               @RequestParam(required = false) String search
    ) {
        return itemService.listBilled(authentication.getName(), pageId);
    }

    @GetMapping("listPending")
    public ItemList listPending(Authentication authentication,
                                @RequestParam(defaultValue = "1") int pageId,
                                @RequestParam(required = false) String search
    ) {
        return itemService.listPending(authentication.getName(), pageId);
    }

    @PutMapping("{id}")
    public FormResult update(Authentication authentication,
                             @PathVariable("id") long id,
                             @RequestBody CreateOrUpdateItem form
    ) {
        return itemService.update(authentication.getName(), id, form);
    }

}

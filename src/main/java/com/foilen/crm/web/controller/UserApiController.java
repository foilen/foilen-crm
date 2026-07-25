package com.foilen.crm.web.controller;

import com.foilen.crm.services.UserService;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/user")
@RestController
@SwaggerExpose
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping("listAll")
    public UserList listAll(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int pageId,
            @RequestParam(required = false) String search
    ) {
        return userService.listAll(authentication.getName(), pageId, search);
    }

    @PutMapping("{id}/admin")
    public FormResult updateAdmin(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody UpdateUserAdminForm form
    ) {
        return userService.updateAdmin(authentication.getName(), id, form);
    }

}

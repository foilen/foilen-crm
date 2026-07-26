package com.foilen.crm.web.controller;

import com.foilen.crm.services.UserService;
import com.foilen.crm.web.model.ChangePasswordForm;
import com.foilen.crm.web.model.LoginForm;
import com.foilen.crm.web.model.LoginWithCodeForm;
import com.foilen.crm.web.model.LoginWithCodeRequestForm;
import com.foilen.crm.web.model.UpdateUserAdminForm;
import com.foilen.crm.web.model.UpdateUserDisabledForm;
import com.foilen.crm.web.model.UserList;
import com.foilen.smalltools.restapi.model.FormResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/user")
@RestController
@SwaggerExpose
public class UserApiController {

    @Autowired
    private UserService userService;

    @PostMapping("changePassword")
    public FormResult changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordForm form
    ) {
        return userService.changePassword(authentication.getName(), form);
    }

    @GetMapping("listAll")
    public UserList listAll(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int pageId,
            @RequestParam(required = false) String search
    ) {
        return userService.listAll(authentication.getName(), pageId, search);
    }

    @PostMapping("login")
    public FormResult login(
            @RequestBody LoginForm form,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return userService.login(form, request, response);
    }

    @PostMapping("loginWithCode")
    public FormResult loginWithCode(
            @RequestBody LoginWithCodeForm form,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return userService.loginWithCode(form, request, response);
    }

    @PostMapping("loginWithCodeRequest")
    public FormResult loginWithCodeRequest(
            @RequestBody LoginWithCodeRequestForm form
    ) {
        return userService.loginWithCodeRequest(form);
    }

    @PostMapping("logout")
    public FormResult logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return userService.logout(request, response);
    }

    @PutMapping("{id}/admin")
    public FormResult updateAdmin(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody UpdateUserAdminForm form
    ) {
        return userService.updateAdmin(authentication.getName(), id, form);
    }

    @PutMapping("{id}/disabled")
    public FormResult updateDisabled(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody UpdateUserDisabledForm form
    ) {
        return userService.updateDisabled(authentication.getName(), id, form);
    }

}

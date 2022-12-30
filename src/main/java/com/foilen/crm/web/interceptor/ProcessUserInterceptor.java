/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.interceptor;

import com.foilen.crm.services.EntitlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class ProcessUserInterceptor implements HandlerInterceptor {

    @Autowired
    private EntitlementService entitlementService;

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            entitlementService.getUserOrFail(authentication);
        }
        return true;
    }

}

package com.foilen.crm.web.interceptor;

import com.foilen.crm.services.EntitlementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class ProcessUserInterceptor implements HandlerInterceptor {

    @Autowired
    private EntitlementService entitlementService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            entitlementService.getUserOrFail(authentication);
        }
        return true;
    }

}

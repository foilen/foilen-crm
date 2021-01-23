/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.restapi.model.ApiError;
import com.foilen.smalltools.tools.AbstractBasics;

public class GeneralHandlerExceptionResolver extends AbstractBasics implements HandlerExceptionResolver {

    private static final MappingJackson2JsonView VIEW = new MappingJackson2JsonView();

    @Autowired
    private MessageSource messageSource;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(VIEW);

        if (e instanceof ErrorMessageException) {
            ErrorMessageException errorMessageException = (ErrorMessageException) e;
            String errorCode = errorMessageException.getMessage();
            ApiError error = new ApiError(messageSource.getMessage(errorCode, new Object[] {}, LocaleContextHolder.getLocale()));
            modelAndView.addObject("error", error);
            logger.error("Error message exception with code {}. Error unique id: {}", errorCode, error.getUniqueId());
        } else {
            ApiError error = new ApiError("Unexpected exception while executing");
            modelAndView.addObject("error", error);
            logger.error("Unexpected exception while executing. Error unique id: {}", error.getUniqueId(), e);
        }
        return modelAndView;
    }

}

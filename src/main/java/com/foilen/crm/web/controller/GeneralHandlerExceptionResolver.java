package com.foilen.crm.web.controller;

import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.restapi.model.ApiError;
import com.foilen.smalltools.tools.AbstractBasics;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class GeneralHandlerExceptionResolver extends AbstractBasics implements HandlerExceptionResolver {

    private static final MappingJackson2JsonView VIEW = new MappingJackson2JsonView();

    @Autowired
    private MessageSource messageSource;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(VIEW);

        if (e instanceof ErrorMessageException errorMessageException) {
            String errorCode = errorMessageException.getMessage();
            ApiError error = new ApiError(messageSource.getMessage(errorCode, new Object[]{}, LocaleContextHolder.getLocale()));
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

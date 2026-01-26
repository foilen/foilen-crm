package com.foilen.crm.web.controller;

import com.foilen.crm.exception.ErrorMessageException;
import com.foilen.smalltools.restapi.model.ApiError;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.JsonTools;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeneralHandlerExceptionResolver extends AbstractBasics implements HandlerExceptionResolver {

    @Autowired
    private MessageSource messageSource;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

        Map<String, Object> model = new HashMap<>();

        if (e instanceof ErrorMessageException errorMessageException) {
            String errorCode = errorMessageException.getMessage();
            ApiError error = new ApiError(messageSource.getMessage(errorCode, new Object[]{}, LocaleContextHolder.getLocale()));
            model.put("error", error);
            logger.error("Error message exception with code {}. Error unique id: {}", errorCode, error.getUniqueId());
        } else {
            ApiError error = new ApiError("Unexpected exception while executing");
            model.put("error", error);
            logger.error("Unexpected exception while executing. Error unique id: {}", error.getUniqueId(), e);
        }

        // Write JSON response directly
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            JsonTools.writeToStream(response.getOutputStream(), model);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return new ModelAndView();
    }

}

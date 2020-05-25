/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm.exception;

public class ErrorMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorMessageException(String message) {
        super(message);
    }

}

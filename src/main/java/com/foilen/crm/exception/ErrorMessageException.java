package com.foilen.crm.exception;

public class ErrorMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorMessageException(String message) {
        super(message);
    }

}

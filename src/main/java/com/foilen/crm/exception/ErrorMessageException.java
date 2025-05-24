package com.foilen.crm.exception;

import java.io.Serial;

public class ErrorMessageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ErrorMessageException(String message) {
        super(message);
    }

}

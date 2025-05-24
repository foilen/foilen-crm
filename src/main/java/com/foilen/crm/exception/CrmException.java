package com.foilen.crm.exception;

import java.io.Serial;

public class CrmException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CrmException() {
    }

    public CrmException(String message) {
        super(message);
    }

    public CrmException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CrmException(Throwable cause) {
        super(cause);
    }

}

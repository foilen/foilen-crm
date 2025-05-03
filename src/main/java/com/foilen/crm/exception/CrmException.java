package com.foilen.crm.exception;

public class CrmException extends RuntimeException {

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

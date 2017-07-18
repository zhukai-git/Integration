package com.zhukai.framework.fast.rest.exception;

public class HttpReadException extends Exception {

    public HttpReadException() {
        super();
    }

    public HttpReadException(Throwable cause) {
        super(cause);
    }

    public HttpReadException(String message) {
        super(message);
    }

    public HttpReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
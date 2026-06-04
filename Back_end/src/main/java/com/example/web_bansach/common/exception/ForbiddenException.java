package com.example.web_bansach.common.exception;

public class ForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

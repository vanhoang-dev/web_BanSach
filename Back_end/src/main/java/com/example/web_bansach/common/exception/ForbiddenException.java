package com.example.web_bansach.common.exception;

// Biểu diễn trường hợp người dùng đã đăng nhập nhưng không có quyền thao tác.
public class ForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

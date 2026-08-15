package com.example.web_bansach.common.exception;

// Biểu diễn yêu cầu chưa được xác thực hoặc phiên đăng nhập không hợp lệ.
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

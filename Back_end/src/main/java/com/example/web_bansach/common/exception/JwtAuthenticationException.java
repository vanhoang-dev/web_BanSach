package com.example.web_bansach.common.exception;

import org.springframework.security.core.AuthenticationException;

// Biểu diễn lỗi JWT không hợp lệ trong chuỗi xác thực Spring Security.
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}


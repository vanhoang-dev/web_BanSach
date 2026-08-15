package com.example.web_bansach.common.exception;

// Biểu diễn dữ liệu đầu vào không vượt qua kiểm tra của ứng dụng.
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

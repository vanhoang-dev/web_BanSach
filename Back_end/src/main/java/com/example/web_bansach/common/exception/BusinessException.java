package com.example.web_bansach.common.exception;

// Biểu diễn lỗi vi phạm quy tắc nghiệp vụ để trả phản hồi 4xx phù hợp.
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}


package com.example.web_bansach.common.exception;

// Biểu diễn lỗi không tìm thấy thực thể theo mã định danh yêu cầu.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}


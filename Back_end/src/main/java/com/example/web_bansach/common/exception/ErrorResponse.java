package com.example.web_bansach.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dữ liệu lỗi được trả về bởi bộ xử lý bảo mật và bộ xử lý ngoại lệ tập trung.
 *
 * Các trường được giữ ngắn gọn và rõ ràng để thuận tiện kiểm tra lỗi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> details;

    public ErrorResponse(String code, String message, int status, LocalDateTime timestamp, String path) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    public static ErrorResponse of(String code, String message, int status, String path) {
        return new ErrorResponse(code, message, status, LocalDateTime.now(), path);
    }

    public static ErrorResponse authenticationError(String message, String path) {
        return of("AUTHENTICATION_ERROR", message, 401, path);
    }

    public static ErrorResponse accessDenied(String message, String path) {
        return of("ACCESS_DENIED", message, 403, path);
    }
}


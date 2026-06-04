package com.example.web_bansach.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper used by controllers.
 *
 * The goal is to keep every endpoint response easy to read:
 * - statusCode: HTTP status code
 * - message: short human-readable message
 * - data: actual payload, if any
 * - timestamp: when the response was created
 * - path: request path, usually filled by error handlers
 * - errors: optional validation or debug details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private Object errors;

    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> of(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(200, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return of(200, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(201, "Created", data);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return failure(statusCode, message);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, Object errors) {
        return failure(statusCode, message, errors);
    }

    public static <T> ApiResponse<T> failure(int statusCode, String message) {
        return of(statusCode, message, null);
    }

    public static <T> ApiResponse<T> failure(int statusCode, String message, Object errors) {
        ApiResponse<T> response = new ApiResponse<>(statusCode, message, null);
        response.setErrors(errors);
        return response;
    }
}

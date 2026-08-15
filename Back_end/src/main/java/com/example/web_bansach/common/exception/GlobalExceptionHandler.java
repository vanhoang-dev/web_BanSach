package com.example.web_bansach.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.web_bansach.common.constant.MessageConstants;
import com.example.web_bansach.common.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Xử lý tập trung các ngoại lệ phát sinh từ toàn bộ endpoint REST.
 * Converts exceptions to standardized ApiResponse format
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        private String requestPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleResourceNotFound(
                        ResourceNotFoundException ex,
                        WebRequest request) {
                log.warn("Resource not found, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.NOT_FOUND.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.RESOURCE_NOT_FOUND);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<?>> handleUnauthorized(
                        UnauthorizedException ex,
                        WebRequest request) {
                log.warn("Unauthorized access, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.UNAUTHORIZED.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.UNAUTHORIZED);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<?>> handleForbidden(
                        ForbiddenException ex,
                        WebRequest request) {
                log.warn("Forbidden access, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.FORBIDDEN.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.FORBIDDEN);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<?>> handleBadCredentials(
                        BadCredentialsException ex,
                        WebRequest request) {
                log.warn("Bad credentials, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.UNAUTHORIZED.value(),
                                MessageConstants.INVALID_CREDENTIALS);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<?>> handleBusinessException(
                        BusinessException ex,
                        WebRequest request) {
                log.warn("Business exception, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.INVALID_REQUEST);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiResponse<?>> handleValidationException(
                        ValidationException ex,
                        WebRequest request) {
                log.warn("Validation exception, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.INVALID_REQUEST);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                log.warn("Request validation failed, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.BAD_REQUEST.value(),
                                MessageConstants.INVALID_REQUEST,
                                errors);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<?>> handleIllegalArgument(
                        IllegalArgumentException ex,
                        WebRequest request) {
                log.warn("Invalid argument, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage() != null ? ex.getMessage() : MessageConstants.INVALID_REQUEST);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<?>> handleGlobalException(
                        Exception ex,
                        WebRequest request) {
                log.error("Unexpected system error, path={}", requestPath(request), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                MessageConstants.INTERNAL_SERVER_ERROR);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
}

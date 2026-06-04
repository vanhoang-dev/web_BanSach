package com.example.web_bansach.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.web_bansach.common.constant.MessageConstants;
import com.example.web_bansach.common.response.ApiResponse;

/**
 * Global exception handler for all REST endpoints
 * Converts exceptions to standardized ApiResponse format
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        private String requestPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleResourceNotFound(
                        ResourceNotFoundException ex,
                        WebRequest request) {
                logger.warn("Resource not found: {}", ex.getMessage());

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
                logger.warn("Unauthorized access: {}", ex.getMessage());

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
                logger.warn("Forbidden access: {}", ex.getMessage());

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
                logger.warn("Bad credentials");

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
                logger.warn("Business exception: {}", ex.getMessage());

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
                logger.warn("Validation exception: {}", ex.getMessage());

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

                logger.warn("Validation failed: {}", errors);

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
                logger.warn("Invalid argument: {}", ex.getMessage());

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
                logger.error("Unexpected error: {}", ex.getMessage(), ex);

                ApiResponse<?> response = ApiResponse.failure(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                MessageConstants.INTERNAL_SERVER_ERROR);
                response.setPath(requestPath(request));

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
}

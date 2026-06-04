package com.example.web_bansach.common.constant;

/**
 * Error and success message constants
 */
public class MessageConstants {

    private MessageConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Error messages
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String FORBIDDEN = "Forbidden";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 6 characters";
    public static final String OUT_OF_STOCK = "Out of stock";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String INVALID_QUANTITY = "Invalid quantity";
    public static final String INVALID_PAGINATION = "Invalid pagination parameters";

    // Success messages
    public static final String CREATED_SUCCESS = "Created successfully";
    public static final String UPDATED_SUCCESS = "Updated successfully";
    public static final String DELETED_SUCCESS = "Deleted successfully";
    public static final String OPERATION_SUCCESS = "Operation completed successfully";
}

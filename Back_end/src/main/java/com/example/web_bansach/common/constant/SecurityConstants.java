package com.example.web_bansach.common.constant;

/**
 * Security-related constants
 */
public class SecurityConstants {

    private SecurityConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String JWT_CLAIM_ROLES = "roles";

    // CORS
    public static final String CORS_ALLOWED_ORIGINS_DEV = "http://localhost:3000";
    public static final String CORS_ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String CORS_ALLOWED_HEADERS = "*";

    // Password
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
}

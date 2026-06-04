package com.example.web_bansach.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * JWT configuration properties
 * Externalize JWT configuration from code
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private long expiration;
    private String refreshTokenSecret;
    private long refreshTokenExpiration;

    public long getExpirationInMillis() {
        return expiration;
    }

    public long getRefreshTokenExpirationInMillis() {
        return refreshTokenExpiration;
    }
}

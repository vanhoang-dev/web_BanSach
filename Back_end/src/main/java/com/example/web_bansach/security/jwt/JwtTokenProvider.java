package com.example.web_bansach.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Token Provider
 * Handles token generation, validation, and parsing
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;
    private final Key accessKey;
    private final Key refreshKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.accessKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        String refreshSecret = jwtProperties.getRefreshTokenSecret() != null
                && !jwtProperties.getRefreshTokenSecret().trim().isEmpty()
                        ? jwtProperties.getRefreshTokenSecret()
                        : jwtProperties.getSecret();
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT token with username
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtProperties.getExpirationInMillis(), accessKey);
    }

    /**
     * Generate JWT token with additional claims
     */
    public String generateToken(String username, Map<String, Object> claims) {
        return createToken(claims, username, jwtProperties.getExpirationInMillis(), accessKey);
    }

    /**
     * Generate JWT token with custom expiration
     */
    public String generateToken(String username, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expirationTime, accessKey);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtProperties.getRefreshTokenExpirationInMillis(), refreshKey);
    }

    public long getRefreshTokenExpirationInMillis() {
        return jwtProperties.getRefreshTokenExpirationInMillis();
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String username, long expirationTime, Key signingKey) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        Claims claims = getAllClaims(token, accessKey);
        return claims.getSubject();
    }

    /**
     * Extract username from a refresh token
     */
    public String extractRefreshUsername(String token) {
        Claims claims = getAllClaims(token, refreshKey);
        return claims.getSubject();
    }

    /**
     * Extract claim from token
     */
    public Object extractClaim(String token, String claimName) {
        Claims claims = getAllClaims(token, accessKey);
        return claims.get(claimName);
    }

    /**
     * Extract all claims from token
     */
    public Claims getAllClaims(String token) {
        return getAllClaims(token, accessKey);
    }

    private Claims getAllClaims(String token, Key signingKey) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            logger.debug("JWT validation error: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaims(token, accessKey);
            return claims.getExpiration().before(new Date());
        } catch (Exception ex) {
            logger.debug("Token expiration check error: {}", ex.getMessage());
            return true;
        }
    }

    /**
     * Validate refresh token
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            logger.debug("Refresh token validation error: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Get token expiration date
     */
    public Date getExpirationDate(String token) {
        Claims claims = getAllClaims(token, accessKey);
        return claims.getExpiration();
    }

    /**
     * Extract roles from token
     */
    public Set<String> extractRoles(String token) {
        Claims claims = getAllClaims(token, accessKey);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?>) {
            return ((Collection<?>) rolesObj).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    /**
     * Extract user ID from token
     */
    public Long extractUserId(String token) {
        Claims claims = getAllClaims(token, accessKey);
        Object userId = claims.get("userId");
        if (userId != null) {
            if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Number) {
                return ((Number) userId).longValue();
            } else if (userId instanceof String) {
                try {
                    return Long.parseLong((String) userId);
                } catch (NumberFormatException e) {
                    logger.debug("Failed to parse userId from token: {}", userId);
                    return null;
                }
            }
        }
        return null;
    }
}

package com.example.web_bansach.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
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
 * Class xử lý những việc cơ bản với JWT:
 * - Tạo mã xác thực sau khi đăng nhập.
 * - Kiểm tra mã xác thực có hợp lệ không.
 * - Lấy thông tin người dùng từ mã xác thực.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;
    private final Key key;

    // Khởi tạo bộ phát JWT từ secret và thời hạn trong cấu hình.
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Phát JWT chứa email, mã người dùng và danh sách quyền cho phiên đăng nhập.
    public String generateToken(String email, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Đọc email chủ thể từ JWT.
    public String extractUsername(String token) {
        Claims claims = getAllClaims(token);
        return claims.getSubject();
    }

    // Xác minh chữ ký rồi đọc toàn bộ thông tin khai báo trong JWT.
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Kiểm tra chữ ký, cấu trúc và hạn dùng của JWT.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            logger.debug("Token không hợp lệ: {}", ex.getMessage());
            return false;
        }
    }

    // Đọc danh sách quyền đã nhúng trong JWT.
    public Set<String> extractRoles(String token) {
        Claims claims = getAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?>) {
            return ((Collection<?>) rolesObj).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    // Đọc ID tài khoản đã nhúng trong JWT.
    public Long extractUserId(String token) {
        Claims claims = getAllClaims(token);
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
                    logger.debug("Không đọc được userId từ token: {}", userId);
                    return null;
                }
            }
        }
        return null;
    }
}

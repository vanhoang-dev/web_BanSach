package com.example.web_bansach.common.util;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.web_bansach.common.exception.BusinessException;

/**
 * Utility class cho HMAC signature generation và verification
 * 
 * Hỗ trợ:
 * - HMAC SHA256: Dùng cho webhook/payment gateway cần chữ ký
 * 
 * Security: Các signature này được dùng để verify callback từ payment gateway
 */
public class HmacUtil {
    private static final Logger logger = LoggerFactory.getLogger(HmacUtil.class);

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Generate HMAC SHA256 signature
     * 
     * @param message   - Dữ liệu cần ký
     * @param secretKey - Secret key từ payment gateway
     * @return Signature (HEX format)
     */
    public static String generateHmacSHA256(String message, String secretKey) {
        try {
            return generateHmac(message, secretKey, HMAC_SHA256);
        } catch (Exception e) {
            logger.error("Error generating HMAC SHA256", e);
            throw new BusinessException("Lỗi khi tạo chữ ký HMAC SHA256");
        }
    }

    /**
     * Verify HMAC SHA256 signature
     * 
     * @param message   - Dữ liệu
     * @param secretKey - Secret key
     * @param signature - Chữ ký cần verify
     * @return true nếu hợp lệ, false nếu không
     */
    public static boolean verifyHmacSHA256(String message, String secretKey, String signature) {
        try {
            String computed = generateHmacSHA256(message, secretKey);
            return constantTimeEquals(computed, signature);
        } catch (Exception e) {
            logger.error("Error verifying HMAC SHA256", e);
            return false;
        }
    }

    /**
     * Tạo signature HMAC với algorithm chỉ định
     */
    private static String generateHmac(String message, String secretKey, String algorithm) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                algorithm);

        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Convert to HEX format
        return bytesToHex(hmacBytes);
    }

    /**
     * Chuyển byte array sang HEX string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * So sánh string an toàn không bị timing attack
     * 
     * @param a - String thứ nhất
     * @param b - String thứ hai
     * @return true nếu bằng nhau, false nếu khác
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }

        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        int result = aBytes.length ^ bBytes.length;
        for (int i = 0; i < Math.min(aBytes.length, bBytes.length); i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }
}

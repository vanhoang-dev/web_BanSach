package com.example.web_bansach.common.logging;

/**
 * Helper nho de log thong tin dinh danh ma khong lo thong tin ca nhan.
 */
public final class LogMaskingUtil {

    private LogMaskingUtil() {
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }

        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }

        String name = trimmed.substring(0, atIndex);
        String domain = trimmed.substring(atIndex);
        if (name.length() <= 2) {
            return name.charAt(0) + "***" + domain;
        }

        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "";
        }

        String digits = phone.replaceAll("\\D", "");
        if (digits.length() <= 4) {
            return "***";
        }

        return "***" + digits.substring(digits.length() - 4);
    }
}

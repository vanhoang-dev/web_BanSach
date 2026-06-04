package com.example.web_bansach.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Utility class for currency operations
 */
public class CurrencyUtils {

    private CurrencyUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("#,##0.00");

    public static BigDecimal calculateDiscount(BigDecimal price, int discountPercent) {
        if (price == null || discountPercent < 0 || discountPercent > 100) {
            return price;
        }

        BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discount);
    }

    public static BigDecimal calculateTax(BigDecimal amount, int taxPercent) {
        if (amount == null || taxPercent < 0) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(BigDecimal.valueOf(taxPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static String formatCurrency(BigDecimal amount) {
        return amount != null ? CURRENCY_FORMATTER.format(amount) : "0.00";
    }

    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    public static BigDecimal roundToTwoDecimals(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}

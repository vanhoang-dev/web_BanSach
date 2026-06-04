package com.example.web_bansach.module.pricing.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.pricing.service.PricingService;
import com.example.web_bansach.module.voucher.entity.Voucher;

/**
 * Xử lý tính toán giá (pricing, discount, voucher)
 */
@Service
public class PricingServiceImpl implements PricingService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int DECIMAL_PLACES = 2;

    @Override
    public BigDecimal calculateBookPrice(Book book) {
        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = book.getPrice();

        if (!hasActiveDiscount(book)) {
            return price;
        }

        BigDecimal discount = calculateDiscount(book);
        return price.subtract(discount);
    }

    @Override
    public BigDecimal calculateDiscount(Book book) {
        if (book == null || !hasActiveDiscount(book)) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = book.getPrice();
        Integer discountPercent = book.getDiscount().getDiscountPercent();

        if (discountPercent == null || discountPercent <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercentBig = new BigDecimal(discountPercent);
        BigDecimal discountAmount = price.multiply(discountPercentBig)
                .divide(HUNDRED, DECIMAL_PLACES, RoundingMode.HALF_UP);

        return discountAmount;
    }

    @Override
    public BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, Voucher voucher) {
        if (totalAmount == null || voucher == null) {
            return BigDecimal.ZERO;
        }

        // Calculate discount amount
        BigDecimal discountPercent = new BigDecimal(voucher.getDiscountPercent());
        BigDecimal discountAmount = totalAmount.multiply(discountPercent)
                .divide(HUNDRED, DECIMAL_PLACES, RoundingMode.HALF_UP);

        // Check if discount exceeds maxDiscount
        if (voucher.getMaxDiscount() != null
                && discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
            return voucher.getMaxDiscount();
        }

        return discountAmount;
    }

    @Override
    public BigDecimal calculateFinalPrice(Book book, Integer quantity, Voucher voucher) {
        if (quantity == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }

        // Calculate book price with discount
        BigDecimal pricePerUnit = calculateBookPrice(book);

        // Calculate subtotal
        BigDecimal subtotal = pricePerUnit.multiply(new BigDecimal(quantity));

        // Apply voucher if provided
        if (voucher != null) {
            BigDecimal voucherDiscount = calculateVoucherDiscount(subtotal, voucher);
            subtotal = subtotal.subtract(voucherDiscount);
        }

        // Ensure price is not negative
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return subtotal;
    }

    @Override
    public boolean hasActiveDiscount(Book book) {
        if (book == null || book.getDiscount() == null) {
            return false;
        }

        return Boolean.TRUE.equals(book.getDiscount().getIsActive());
    }

    @Override
    public Integer getDiscountPercent(Book book) {
        if (!hasActiveDiscount(book)) {
            return 0;
        }

        Integer percent = book.getDiscount().getDiscountPercent();
        return percent != null ? percent : 0;
    }
}

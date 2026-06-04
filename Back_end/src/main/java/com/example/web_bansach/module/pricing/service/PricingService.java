package com.example.web_bansach.module.pricing.service;

import java.math.BigDecimal;

import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.voucher.entity.Voucher;

/**
 * Service tính toán giá (pricing, discount, voucher)
 * Tách riêng logic để tái sử dụng ở multiple places (cart, order, etc.)
 */
public interface PricingService {

    /**
     * Tính giá sách sau khi áp dụng discount
     * 
     * @param book - Đối tượng sách
     * @return Giá sau khi áp dụng discount (nếu có)
     */
    BigDecimal calculateBookPrice(Book book);

    /**
     * Tính tiền discount từ discount object
     * 
     * @param book - Đối tượng sách
     * @return Số tiền discount
     */
    BigDecimal calculateDiscount(Book book);

    /**
     * Tính tiền discount từ voucher
     * 
     * @param totalAmount - Tổng tiền trước khi áp voucher
     * @param voucher     - Đối tượng voucher
     * @return Số tiền discount từ voucher (có giới hạn maxDiscount)
     */
    BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, Voucher voucher);

    /**
     * Tính giá cuối cùng sau khi áp dụng discount và voucher
     * 
     * @param book     - Đối tượng sách
     * @param quantity - Số lượng
     * @param voucher  - Voucher (có thể null)
     * @return Giá cuối cùng
     */
    BigDecimal calculateFinalPrice(Book book, Integer quantity, Voucher voucher);

    /**
     * Check xem book có discount active không
     * 
     * @param book - Đối tượng sách
     * @return true nếu book có discount active
     */
    boolean hasActiveDiscount(Book book);

    /**
     * Lấy phần trăm discount của book
     * 
     * @param book - Đối tượng sách
     * @return Phần trăm discount, 0 nếu không có discount
     */
    Integer getDiscountPercent(Book book);
}

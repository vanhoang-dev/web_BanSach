package com.example.web_bansach.module.pricing.service;

import java.math.BigDecimal;

import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.voucher.entity.Voucher;

/**
 * Dịch vụ tính giá sản phẩm, mức giảm giá và voucher.
 * Tách riêng logic tính giá để tái sử dụng tại giỏ hàng, đơn hàng và các nơi khác.
 */
public interface PricingService {

    /**
     * Tính giá sách sau khi áp dụng chương trình giảm giá.
     * 
     * @param book đối tượng sách
     * @return giá sau khi áp dụng chương trình giảm giá nếu có
     */
    BigDecimal calculateBookPrice(Book book);

    /**
     * Tính số tiền được giảm từ chương trình giảm giá của sách.
     * 
     * @param book đối tượng sách
     * @return số tiền được giảm
     */
    BigDecimal calculateDiscount(Book book);

    /**
     * Tính số tiền được giảm từ voucher.
     * 
     * @param totalAmount - Tổng tiền trước khi áp voucher
     * @param voucher     - Đối tượng voucher
     * @return số tiền được giảm từ voucher, không vượt quá mức giảm tối đa
     */
    BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, Voucher voucher);

    /**
     * Tính giá cuối cùng sau khi áp dụng chương trình giảm giá và voucher.
     * 
     * @param book đối tượng sách
     * @param quantity - Số lượng
     * @param voucher  - Voucher (có thể null)
     * @return giá cuối cùng
     */
    BigDecimal calculateFinalPrice(Book book, Integer quantity, Voucher voucher);

    /**
     * Kiểm tra sách có chương trình giảm giá đang hoạt động hay không.
     * 
     * @param book đối tượng sách
     * @return {@code true} nếu sách có chương trình giảm giá đang hoạt động
     */
    boolean hasActiveDiscount(Book book);

    /**
     * Lấy phần trăm giảm giá của sách.
     * 
     * @param book đối tượng sách
     * @return phần trăm giảm giá hoặc 0 nếu không có chương trình giảm giá
     */
    Integer getDiscountPercent(Book book);
}

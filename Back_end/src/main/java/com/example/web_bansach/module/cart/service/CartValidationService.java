package com.example.web_bansach.module.cart.service;

/**
 * Dịch vụ kiểm tra dữ liệu giỏ hàng.
 */
public interface CartValidationService {

    /**
     * Kiểm tra số lượng của sản phẩm trong giỏ.
     * 
     * @param quantity - Số lượng
     * @throws BusinessException nếu không hợp lệ
     */
    void validateQuantity(Integer quantity);

    /**
     * Kiểm tra mã định danh của sách.
     * 
     * @param bookId - Book ID
     * @throws ResourceNotFoundException nếu sách không tồn tại
     */
    void validateBookExists(Long bookId);

    /**
     * Kiểm tra sản phẩm trong giỏ có tồn tại hay không.
     * 
     * @param itemId - CartItem ID
     * @throws ResourceNotFoundException nếu item không tồn tại
     */
    void validateCartItemExists(Long itemId);
}

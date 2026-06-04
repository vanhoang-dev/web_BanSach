package com.example.web_bansach.module.cart.service;

/**
 * Service xử lý validation cho Cart
 */
public interface CartValidationService {

    /**
     * Validate cart item quantity
     * 
     * @param quantity - Số lượng
     * @throws BusinessException nếu không hợp lệ
     */
    void validateQuantity(Integer quantity);

    /**
     * Validate book ID
     * 
     * @param bookId - Book ID
     * @throws ResourceNotFoundException nếu book không tồn tại
     */
    void validateBookExists(Long bookId);

    /**
     * Validate cart item exists
     * 
     * @param itemId - CartItem ID
     * @throws ResourceNotFoundException nếu item không tồn tại
     */
    void validateCartItemExists(Long itemId);
}

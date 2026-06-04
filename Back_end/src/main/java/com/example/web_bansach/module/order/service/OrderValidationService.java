package com.example.web_bansach.module.order.service;

import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;

/**
 * Service xử lý validation cho Order
 */
public interface OrderValidationService {

    /**
     * Validate thông tin khi tạo order
     * 
     * @param request - CreateOrderRequest
     * @throws BusinessException nếu validation thất bại
     */
    void validateCreateOrder(CreateOrderRequest request);

    /**
     * Validate order ID
     * 
     * @param orderId - Order ID
     * @throws ResourceNotFoundException nếu order không tồn tại
     */
    void validateOrderExists(Long orderId);
}

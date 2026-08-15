package com.example.web_bansach.module.order.service;

import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;

/**
 * Dịch vụ kiểm tra dữ liệu đơn hàng.
 */
public interface OrderValidationService {

    /**
     * Kiểm tra thông tin khi tạo đơn hàng.
     * 
     * @param request dữ liệu yêu cầu tạo đơn hàng
     * @throws BusinessException nếu validation thất bại
     */
    void validateCreateOrder(CreateOrderRequest request);

    /**
     * Kiểm tra mã định danh đơn hàng.
     * 
     * @param orderId - Order ID
     * @throws ResourceNotFoundException nếu đơn hàng không tồn tại
     */
    void validateOrderExists(Long orderId);
}

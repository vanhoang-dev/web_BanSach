package com.example.web_bansach.module.order.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.order.service.OrderValidationService;

/**
 * Xử lý validation cho Order
 */
@Service
public class OrderValidationServiceImpl implements OrderValidationService {

    private final OrderRepository orderRepository;

    public OrderValidationServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public void validateCreateOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new BusinessException("Thông tin đơn hàng không hợp lệ");
        }

        if (request.getShippingAddress() == null || request.getShippingAddress().trim().isEmpty()) {
            throw new BusinessException("Địa chỉ giao hàng không được để trống");
        }

        if (request.getReceiverName() == null || request.getReceiverName().trim().isEmpty()) {
            throw new BusinessException("Tên người nhận không được để trống");
        }

        if (request.getReceiverPhone() == null || request.getReceiverPhone().trim().isEmpty()) {
            throw new BusinessException("Số điện thoại người nhận không được để trống");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void validateOrderExists(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("ID đơn hàng không hợp lệ");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }
    }
}

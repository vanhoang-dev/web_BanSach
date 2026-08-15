package com.example.web_bansach.module.order.service.impl;

import java.math.BigDecimal;

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
// Tập trung các kiểm tra đầu vào và sự tồn tại của đơn hàng.
public class OrderValidationServiceImpl implements OrderValidationService {
    private static final int MAX_RECEIVER_NAME_LENGTH = 255;
    private static final int MAX_SHIPPING_ADDRESS_LENGTH = 500;
    private static final int MAX_SHIPPING_METHOD_LENGTH = 100;
    private static final String VIETNAM_PHONE_PATTERN = "^(0|\\+84)[0-9]{9}$";

    private final OrderRepository orderRepository;

    // Khởi tạo bộ kiểm tra với repository đơn hàng.
    public OrderValidationServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    @Override
    // Kiểm tra thông tin người nhận, vận chuyển và phí trước khi tạo đơn.
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

        if (request.getReceiverName().trim().length() > MAX_RECEIVER_NAME_LENGTH) {
            throw new BusinessException("Ten nguoi nhan toi da 255 ky tu");
        }

        if (!request.getReceiverPhone().trim().matches(VIETNAM_PHONE_PATTERN)) {
            throw new BusinessException("So dien thoai khong hop le");
        }

        if (request.getShippingAddress().trim().length() > MAX_SHIPPING_ADDRESS_LENGTH) {
            throw new BusinessException("Dia chi giao hang toi da 500 ky tu");
        }

        if (request.getShippingMethod() != null
                && request.getShippingMethod().trim().length() > MAX_SHIPPING_METHOD_LENGTH) {
            throw new BusinessException("Phuong thuc giao hang toi da 100 ky tu");
        }

        if (request.getShippingFee() != null && request.getShippingFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Phi van chuyen phai lon hon hoac bang 0");
        }
    }

    @Transactional(readOnly = true)
    @Override
    // Bảo đảm ID đơn hàng tồn tại trước thao tác quản trị.
    public void validateOrderExists(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("ID đơn hàng không hợp lệ");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }
    }
}

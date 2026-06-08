package com.example.web_bansach.module.payment.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.infrastructure.payment.PaymentGateway;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.entity.Payment;
import com.example.web_bansach.module.payment.repository.PaymentRepository;
import com.example.web_bansach.module.payment.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final RealtimeNotificationService realtimeNotificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentGateway paymentGateway,
            RealtimeNotificationService realtimeNotificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.realtimeNotificationService = realtimeNotificationService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResponse initiatePayment(String userEmail, PaymentRequest request) throws Exception {
        if (request == null) {
            throw new BusinessException("Thông tin thanh toán không hợp lệ");
        }

        validatePaymentRequest(request);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        validateOrderCanBePaidByUser(order, userEmail, request.getAmount());

        String paymentUrl = paymentGateway.initiatePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getReturnUrl(),
                request.getDescription());

        String transactionId = "SEP-" + order.getId();

        Payment payment = paymentRepository.findByOrder_Id(order.getId()).orElseGet(Payment::new);
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod("SEPAY");
        payment.setStatus("PENDING");
        payment.setPaymentUrl(paymentUrl);
        payment.setTransactionId(transactionId);

        Payment savedPayment = paymentRepository.save(payment);

        realtimeNotificationService.publishPaymentEvent(
                "PAYMENT_INITIATED",
                savedPayment.getId(),
                order.getId(),
                "Thanh toán đã được khởi tạo",
                "PENDING",
                java.util.Map.of(
                        "paymentId", savedPayment.getId(),
                        "orderId", order.getId(),
                        "transactionId", transactionId,
                        "amount", request.getAmount(),
                        "status", "PENDING"));

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(savedPayment.getId());
        response.setPaymentUrl(paymentUrl);
        response.setTransactionId(transactionId);
        response.setAmount(request.getAmount());
        response.setStatus("PENDING");
        response.setMessage("Chuyển hướng tới trang thanh toán...");

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean verifyPaymentCallback(String transactionId, BigDecimal amount, String signature) {
        if (transactionId == null || amount == null || signature == null) {
            return false;
        }

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElse(null);

        if (payment == null) {
            return false;
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(amount) != 0) {
            return false;
        }

        return paymentGateway.verifyPayment(transactionId, amount, signature);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        return toPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentStatusByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        return toPaymentResponse(payment);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentUrl(payment.getPaymentUrl());
        response.setStatus(payment.getStatus() != null ? payment.getStatus() : "PENDING");

        return response;
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getOrderId() == null || request.getOrderId() <= 0) {
            throw new BusinessException("ID đơn hàng không hợp lệ");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền thanh toán phải lớn hơn 0");
        }

        if (request.getReturnUrl() == null || request.getReturnUrl().trim().isEmpty()) {
            throw new BusinessException("URL trả về không hợp lệ");
        }
    }

    private void validateOrderCanBePaidByUser(Order order, String userEmail, BigDecimal amount) {
        if (order.getUser() == null || order.getUser().getEmail() == null
                || !order.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Bạn không có quyền thanh toán đơn hàng này");
        }

        if (order.getStatus() == null || !"PENDING".equals(order.getStatus().name())) {
            throw new BusinessException("Chỉ có thể thanh toán đơn hàng đang chờ xử lý");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(amount) != 0) {
            throw new BusinessException("Số tiền thanh toán không khớp với tổng tiền đơn hàng");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePaymentStatus(String transactionId, String status, String signature) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        payment.setStatus(status);
        payment.setCallbackSignature(signature);
        payment.setCallbackReceivedAt(java.time.LocalDateTime.now());
        payment.setCallbackVerified(true);

        if ("SUCCESS".equals(status)) {
            payment.setPaidAt(java.time.LocalDateTime.now());
            Order order = payment.getOrder();
            if (order != null && order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setUpdatedAt(java.time.LocalDateTime.now());
                orderRepository.save(order);
            }
        }

        paymentRepository.save(payment);

        realtimeNotificationService.publishPaymentEvent(
                "PAYMENT_STATUS_UPDATED",
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                "Trạng thái thanh toán đã thay đổi",
                status,
                java.util.Map.of(
                        "paymentId", payment.getId(),
                        "transactionId", transactionId,
                        "status", status,
                        "signatureVerified", true));
    }
}

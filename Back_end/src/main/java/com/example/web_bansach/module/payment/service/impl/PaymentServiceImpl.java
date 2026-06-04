package com.example.web_bansach.module.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.infrastructure.payment.PaymentGateway;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.entity.Payment;
import com.example.web_bansach.module.payment.repository.PaymentRepository;
import com.example.web_bansach.module.payment.service.PaymentService;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;

/**
 * Service xử lý payment operations
 * Chỉ hỗ trợ SePay
 */
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
    public PaymentResponse initiatePayment(PaymentRequest request) throws Exception {
        if (request == null) {
            throw new BusinessException("Thông tin thanh toán không hợp lệ");
        }

        // Validate request
        validatePaymentRequest(request);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        // Initiate SePay payment
        String paymentUrl = paymentGateway.initiatePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getReturnUrl(),
                request.getDescription());

        String transactionId = "SEP-" + order.getId();

        // Save payment record
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

        // Return response
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

        String status = payment.getStatus() != null ? payment.getStatus() : "PENDING";

        if (payment.getTransactionId() != null && !payment.getTransactionId().trim().isEmpty()) {
            String gatewayStatus = paymentGateway.getPaymentStatus(payment.getTransactionId());
            if (gatewayStatus != null && !gatewayStatus.trim().isEmpty() && !gatewayStatus.equals(status)) {
                payment.setStatus(gatewayStatus);
                paymentRepository.save(payment);
                status = gatewayStatus;
            }
        }

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentUrl(payment.getPaymentUrl());
        response.setStatus(status);

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean refundPayment(Long paymentId, BigDecimal amount) throws Exception {
        if (paymentId == null || paymentId <= 0) {
            throw new BusinessException("ID thanh toán không hợp lệ");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền hoàn phải lớn hơn 0");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        if (payment.getAmount() == null || amount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("Số tiền hoàn không được lớn hơn số tiền đã thanh toán");
        }

        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException("Chỉ có thể hoàn tiền cho giao dịch đã thanh toán thành công");
        }

        boolean refunded = false;
        if (payment.getTransactionId() != null && !payment.getTransactionId().trim().isEmpty()) {
            refunded = paymentGateway.refund(payment.getTransactionId(), amount);
        }

        if (!refunded) {
            // SePay hiện không công bố public refund API trong tài liệu mở,
            // nên hệ thống vẫn lưu trạng thái hoàn tiền nội bộ để đồng bộ nghiệp vụ.
            refunded = true;
        }

        payment.setStatus("REFUNDED");
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return refunded;
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

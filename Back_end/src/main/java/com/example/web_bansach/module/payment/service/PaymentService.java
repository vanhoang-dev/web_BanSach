package com.example.web_bansach.module.payment.service;

import java.math.BigDecimal;

import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse initiatePayment(String userEmail, PaymentRequest request) throws Exception;

    boolean verifyPaymentCallback(String transactionId, BigDecimal amount, String signature);

    PaymentResponse getPaymentStatus(Long paymentId);

    PaymentResponse getPaymentStatusByOrderId(Long orderId);

    void updatePaymentStatus(String transactionId, String status, String signature);
}

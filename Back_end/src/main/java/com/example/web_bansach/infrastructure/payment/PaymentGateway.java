package com.example.web_bansach.infrastructure.payment;

import java.math.BigDecimal;

// Trừu tượng hóa cổng thanh toán để service không phụ thuộc trực tiếp SePay.
public interface PaymentGateway {

    String initiatePayment(Long orderId, BigDecimal amount, String returnUrl, String description) throws Exception;

    boolean verifyPayment(String transactionId, BigDecimal amount, String signature);
}

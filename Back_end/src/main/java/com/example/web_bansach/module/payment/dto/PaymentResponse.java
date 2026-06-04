package com.example.web_bansach.module.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response sau khi khởi tạo payment
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private String paymentUrl; // URL để redirect người dùng thanh toán
    private String transactionId; // ID giao dịch từ payment gateway
    private BigDecimal amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String message;
}

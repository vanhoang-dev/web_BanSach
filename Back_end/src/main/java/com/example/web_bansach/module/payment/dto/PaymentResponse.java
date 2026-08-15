package com.example.web_bansach.module.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dữ liệu phản hồi sau khi khởi tạo thanh toán.
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
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String message;
}

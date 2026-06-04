package com.example.web_bansach.module.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request để khởi tạo payment
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String returnUrl; // URL trả về sau khi thanh toán
    private String description; // Mô tả giao dịch
}

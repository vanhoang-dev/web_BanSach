package com.example.web_bansach.module.payment.service.email;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentSuccessEmailModel(
        String customerName,
        Long orderId,
        BigDecimal amount,
        String transactionCode,
        LocalDateTime paidAt,
        List<Item> items) {

    public PaymentSuccessEmailModel {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public record Item(String title, Integer quantity, BigDecimal unitPrice) {
    }
}

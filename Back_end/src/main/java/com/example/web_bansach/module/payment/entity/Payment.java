package com.example.web_bansach.module.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.web_bansach.module.order.entity.Order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    private BigDecimal amount;
    private String paymentMethod; // SEPAY only
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED, CANCELLED

    // Transaction tracking
    private String transactionId; // ID từ payment gateway
    private String paymentUrl; // URL để redirect người dùng

    // Callback verification
    private String callbackSignature; // Chữ ký từ callback
    private LocalDateTime callbackReceivedAt; // Thời gian nhận callback
    private Boolean callbackVerified; // Trạng thái xác minh callback

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt; // Thời gian thanh toán thành công

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
        if (callbackVerified == null) {
            callbackVerified = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

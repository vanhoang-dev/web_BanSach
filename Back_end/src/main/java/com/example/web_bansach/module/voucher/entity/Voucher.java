package com.example.web_bansach.module.voucher.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Ánh xạ kho voucher chung với số lượng phát hành và điều kiện giảm giá.
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code; // Mã voucher (duy nhất)

    @Column(name = "discount_percent")
    private Integer discountPercent; // Phần trăm giảm giá (0-100)

    @Column(name = "max_discount")
    private BigDecimal maxDiscount; // Số tiền giảm tối đa

    @Column(name = "quantity")
    private Integer quantity; // Số lượng voucher khả dụng

    @Column(name = "expired_at")
    private LocalDate expiredAt; // Ngày hết hạn

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

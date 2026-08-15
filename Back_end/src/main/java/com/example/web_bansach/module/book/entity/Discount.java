package com.example.web_bansach.module.book.entity;

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
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Ánh xạ chương trình giảm trực tiếp trên giá sách trong một khoảng thời gian.
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id; // ID chương trình giảm giá

    private String name; // Tên chương trình
    private Integer discountPercent; // Phần trăm giảm giá
    private LocalDateTime startDate; // Ngày bắt đầu
    private LocalDateTime endDate; // Ngày kết thúc
    private Boolean isActive = true; // Trạng thái kích hoạt
}

package com.example.web_bansach.module.voucher.entity;

import java.time.LocalDateTime;

import com.example.web_bansach.module.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_vouchers", uniqueConstraints = @UniqueConstraint(
        name = "uk_user_vouchers_user_voucher", columnNames = { "user_id", "voucher_id" }))
@Getter
@Setter
@NoArgsConstructor
// Ánh xạ voucher người dùng đã nhận và trạng thái đã sử dụng hay chưa.
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(nullable = false)
    private Boolean used = false;
}

package com.example.web_bansach.module.auth.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
// Lưu token khôi phục mật khẩu, chủ sở hữu, hạn dùng và trạng thái sử dụng.
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    // Gán thời gian tạo/cập nhật và giá trị mặc định trước khi insert vào database.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (used == null) {
            used = false;
        }
    }

    @PreUpdate
    // Cập nhật thời điểm thay đổi gần nhất trước khi update bản ghi.
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

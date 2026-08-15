package com.example.web_bansach.module.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.auth.entity.PasswordResetToken;

@Repository
// Truy cập bảng token khôi phục mật khẩu và hỗ trợ tìm token người dùng gửi lên.
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Tìm bản ghi reset token để kiểm tra hạn dùng và trạng thái đã sử dụng.
    Optional<PasswordResetToken> findByToken(String token);
}

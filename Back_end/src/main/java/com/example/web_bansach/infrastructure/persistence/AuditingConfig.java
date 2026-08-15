package com.example.web_bansach.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Cấu hình tự động ghi nhận thời gian tạo và cập nhật dữ liệu bằng JPA.
 * Bật cơ chế tự động điền các trường theo dõi thời gian thay đổi dữ liệu.
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}

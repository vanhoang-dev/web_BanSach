package com.example.web_bansach.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Lớp thực thể cơ sở chứa các trường thời gian tạo, cập nhật và xóa.
 * Các thực thể kế thừa lớp này để thống nhất cơ chế theo dõi dữ liệu.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Kiểm tra thực thể đã bị xóa mềm hay chưa.
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Đánh dấu thực thể đã bị xóa theo cơ chế xóa mềm.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Khôi phục thực thể đã bị xóa mềm.
     */
    public void restore() {
        this.deletedAt = null;
    }
}

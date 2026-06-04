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
 * Base entity class with audit fields (createdAt, updatedAt, deletedAt)
 * All entities should extend this class for consistency
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
     * Check if entity is soft deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Soft delete entity
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore soft deleted entity
     */
    public void restore() {
        this.deletedAt = null;
    }
}

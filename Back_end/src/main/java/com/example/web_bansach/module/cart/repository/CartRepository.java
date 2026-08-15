package com.example.web_bansach.module.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.cart.entity.Cart;

/**
 * Kho truy cập dữ liệu giỏ hàng.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Tìm giỏ hàng của người dùng.
     */
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);

    /**
     * Kiểm tra người dùng đã có giỏ hàng chưa.
     */
    boolean existsByUserId(Long userId);
}

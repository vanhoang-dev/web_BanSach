package com.example.web_bansach.module.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.cart.entity.CartItem;

@Repository
// Truy cập các dòng giỏ theo cartId, bookId và hỗ trợ xóa theo giỏ.
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.book b LEFT JOIN FETCH b.discount WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithBook(@Param("cartId") Long cartId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}

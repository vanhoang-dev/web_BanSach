package com.example.web_bansach.module.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.order.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.book WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithBook(@Param("orderId") Long orderId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE EXISTS " +
            "(SELECT p.id FROM Payment p WHERE p.order = oi.order AND p.status = 'SUCCESS')")
    long sumSoldQuantityForSuccessfulPayments();
}

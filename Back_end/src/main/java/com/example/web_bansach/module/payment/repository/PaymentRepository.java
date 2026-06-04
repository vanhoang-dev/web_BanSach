package com.example.web_bansach.module.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tìm payment theo order ID
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Optional<Payment> findByOrder_Id(@Param("orderId") Long orderId);

    /**
     * Tìm payment theo transaction ID từ gateway
     */
    Optional<Payment> findByTransactionId(String transactionId);
}

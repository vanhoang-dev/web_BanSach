package com.example.web_bansach.module.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Lấy danh sách đơn hàng có phân trang của người dùng.
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Tìm đơn hàng của người dùng theo mã định danh.
     */
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
    Order findByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.book.id = :bookId AND oi.order.status = com.example.web_bansach.module.order.entity.OrderStatus.COMPLETED")
    long countCompletedItemsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    List<Order> findTop5ByOrderByOrderDateDesc();
}

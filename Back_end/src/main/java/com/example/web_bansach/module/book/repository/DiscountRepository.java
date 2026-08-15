package com.example.web_bansach.module.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.book.entity.Discount;

@Repository
// Truy cập các chương trình giảm giá gắn trực tiếp với sách.
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}

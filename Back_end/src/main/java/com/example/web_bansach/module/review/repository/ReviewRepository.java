package com.example.web_bansach.module.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy danh sách đánh giá có phân trang của một cuốn sách.
    Page<Review> findByBookId(Long bookId, Pageable pageable);

    // Lấy danh sách đánh giá có phân trang của một người dùng.
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Kiểm tra người dùng đã đánh giá cuốn sách hay chưa.
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);

    // Lấy đánh giá kèm thông tin người dùng và sách.
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.id = :id")
    Optional<Review> findByIdWithJoin(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.book.deletedAt IS NULL")
    long countByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Lấy toàn bộ đánh giá kèm thông tin người dùng và sách.
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
    List<Review> findByBookIdWithJoin(@Param("bookId") Long bookId);

    // Đếm số lượng đánh giá của một cuốn sách.
    long countByBookId(Long bookId);

    // Tính điểm đánh giá trung bình của một cuốn sách.
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);
}

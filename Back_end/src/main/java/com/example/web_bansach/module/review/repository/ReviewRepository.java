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

    // Find all reviews for a book (paged)
    Page<Review> findByBookId(Long bookId, Pageable pageable);

    // Find all reviews by a user (paged)
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Check if user reviewed a book
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);

    // Get reviews with user and book info
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.id = :id")
    Optional<Review> findByIdWithJoin(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.book.id = :bookId AND r.book.deletedAt IS NULL")
    long countByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Get all reviews with user and book info
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
    List<Review> findByBookIdWithJoin(@Param("bookId") Long bookId);

    // Count reviews for a book
    long countByBookId(Long bookId);

    // Get average rating for a book
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);
}

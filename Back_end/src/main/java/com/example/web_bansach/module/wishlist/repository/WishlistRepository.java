package com.example.web_bansach.module.wishlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.wishlist.entity.Wishlist;
import com.example.web_bansach.module.wishlist.entity.WishlistId;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    // Tìm mục yêu thích theo người dùng và sách.
    Optional<Wishlist> findByIdUserIdAndIdBookId(Long userId, Long bookId);

    // Lấy danh sách yêu thích có phân trang của người dùng.
    @Query("SELECT w FROM Wishlist w JOIN FETCH w.book WHERE w.id.userId = :userId ORDER BY w.createdAt DESC")
    Page<Wishlist> findByIdUserId(@Param("userId") Long userId, Pageable pageable);

    // Lấy toàn bộ danh sách yêu thích không phân trang để kiểm tra nhanh.
    @Query("SELECT w FROM Wishlist w WHERE w.id.userId = :userId")
    List<Wishlist> findAllByIdUserId(@Param("userId") Long userId);

    // Kiểm tra sách có nằm trong danh sách yêu thích của người dùng hay không.
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.id.userId = :userId AND w.id.bookId = :bookId")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Đếm số sách trong danh sách yêu thích của người dùng.
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.id.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    // Xóa một mục khỏi danh sách yêu thích.
    void deleteByIdUserIdAndIdBookId(Long userId, Long bookId);
}




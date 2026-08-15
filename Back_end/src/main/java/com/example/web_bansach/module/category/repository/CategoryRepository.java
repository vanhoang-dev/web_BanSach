package com.example.web_bansach.module.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.category.entity.Category;

/**
 * Kho truy cập dữ liệu của thực thể danh mục.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Tìm các danh mục đang hoạt động theo từng trang.
     */
    Page<Category> findAllByIsActiveTrue(Pageable pageable);

    /**
     * Tìm danh mục theo tên.
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.name LIKE %:keyword%")
    Page<Category> searchByName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Kiểm tra danh mục có tồn tại theo tên hay không.
     */
    boolean existsByName(String name);

    /**
     * Kiểm tra tên danh mục đã thuộc về một mã định danh khác hay chưa.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

    /**
     * Kiểm tra danh mục có đang được sách sử dụng hay không.
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Category c LEFT JOIN Book b ON c.id = b.category.id WHERE c.id = :id")
    boolean isInUse(@Param("id") Long id);
}

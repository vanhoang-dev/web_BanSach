package com.example.web_bansach.module.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.category.entity.Category;

/**
 * Repository for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all active categories with pagination
     */
    Page<Category> findAllByIsActiveTrue(Pageable pageable);

    /**
     * Search categories by name
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.name LIKE %:keyword%")
    Page<Category> searchByName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Check if category exists by name
     */
    boolean existsByName(String name);

    /**
     * Check if category exists by name and different ID
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

    /**
     * Check if category is in use (has books)
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Category c LEFT JOIN Book b ON c.id = b.category.id WHERE c.id = :id")
    boolean isInUse(@Param("id") Long id);
}

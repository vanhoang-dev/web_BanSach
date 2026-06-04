package com.example.web_bansach.module.category.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.category.dto.response.CategoryResponse;

/**
 * Query Service Interface for Category
 * Handles all read operations
 */
public interface CategoryQueryService {

    /**
     * Get all active categories with pagination
     */
    Page<CategoryResponse> getAllActive(int pageNumber, int pageSize);

    /**
     * Get category by ID
     */
    CategoryResponse getById(Long id);

    /**
     * Search categories by name
     */
    Page<CategoryResponse> search(String keyword, int pageNumber, int pageSize);

    /**
     * Check if category exists
     */
    boolean exists(Long id);
}

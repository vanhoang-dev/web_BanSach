package com.example.web_bansach.module.category.service;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;

/**
 * Command Service Interface for Category
 * Handles all write operations (create, update, delete)
 */
public interface CategoryCommandService {

    /**
     * Create new category
     */
    CategoryResponse create(CreateCategoryRequest request);

    /**
     * Update existing category
     */
    CategoryResponse update(Long id, UpdateCategoryRequest request);

    /**
     * Soft delete category
     */
    void delete(Long id);

    /**
     * Activate category
     */
    void activate(Long id);

    /**
     * Deactivate category
     */
    void deactivate(Long id);
}

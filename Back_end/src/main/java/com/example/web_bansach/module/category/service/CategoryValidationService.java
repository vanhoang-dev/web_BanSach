package com.example.web_bansach.module.category.service;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;

/**
 * Validation Service Interface for Category
 * Handles all business rule validation
 */
public interface CategoryValidationService {

    /**
     * Validate create request
     */
    void validateCreate(CreateCategoryRequest request);

    /**
     * Validate update request
     */
    void validateUpdate(Long id, UpdateCategoryRequest request);

    /**
     * Validate delete operation
     */
    void validateDelete(Long id);
}

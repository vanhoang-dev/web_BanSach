package com.example.web_bansach.module.category.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.entity.Category;
import com.example.web_bansach.module.category.mapper.CategoryMapper;
import com.example.web_bansach.module.category.repository.CategoryRepository;
import com.example.web_bansach.module.category.service.CategoryCommandService;
import com.example.web_bansach.module.category.service.CategoryValidationService;
import com.example.web_bansach.common.exception.ResourceNotFoundException;

/**
 * Implementation of CategoryCommandService
 */
@Service
@Transactional
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidationService validationService;

    public CategoryCommandServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            CategoryValidationService validationService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.validationService = validationService;
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        // Validate request
        validationService.validateCreate(request);

        // Map request to entity
        Category category = categoryMapper.toEntity(request);

        // Save to database
        Category saved = categoryRepository.save(category);

        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        // Get existing category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Validate update request
        validationService.validateUpdate(id, request);

        // Update fields
        categoryMapper.updateEntity(request, category);

        // Save changes
        Category updated = categoryRepository.save(category);

        return categoryMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Validate delete operation
        validationService.validateDelete(id);

        // Soft delete
        category.softDelete();
        categoryRepository.save(category);
    }

    @Override
    public void activate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setIsActive(true);
        categoryRepository.save(category);
    }

    @Override
    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setIsActive(false);
        categoryRepository.save(category);
    }
}

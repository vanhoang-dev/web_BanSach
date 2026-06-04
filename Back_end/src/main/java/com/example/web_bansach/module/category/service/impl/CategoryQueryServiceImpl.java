package com.example.web_bansach.module.category.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.entity.Category;
import com.example.web_bansach.module.category.mapper.CategoryMapper;
import com.example.web_bansach.module.category.repository.CategoryRepository;
import com.example.web_bansach.module.category.service.CategoryQueryService;
import com.example.web_bansach.common.constant.AppConstants;
import com.example.web_bansach.common.constant.MessageConstants;
import com.example.web_bansach.common.exception.ResourceNotFoundException;

/**
 * Implementation of CategoryQueryService
 */
@Service
@Transactional(readOnly = true)
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryQueryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Page<CategoryResponse> getAllActive(int pageNumber, int pageSize) {
        validatePagination(pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Category> categories = categoryRepository.findAllByIsActiveTrue(pageable);

        return categories.map(categoryMapper::toDto);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        return categoryMapper.toDto(category);
    }

    @Override
    public Page<CategoryResponse> search(String keyword, int pageNumber, int pageSize) {
        validatePagination(pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Category> categories = categoryRepository.searchByName(keyword, pageable);

        return categories.map(categoryMapper::toDto);
    }

    @Override
    public boolean exists(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Validate pagination parameters
     */
    private void validatePagination(int pageNumber, int pageSize) {
        if (pageNumber < AppConstants.DEFAULT_PAGE_NUMBER || pageSize <= 0) {
            throw new IllegalArgumentException(MessageConstants.INVALID_PAGINATION);
        }

        if (pageSize > AppConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page size must not exceed " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}

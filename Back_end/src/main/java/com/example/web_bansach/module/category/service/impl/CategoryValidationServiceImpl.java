package com.example.web_bansach.module.category.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.repository.CategoryRepository;
import com.example.web_bansach.module.category.service.CategoryValidationService;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ValidationException;

/**
 * Implementation of CategoryValidationService
 */
@Service
@Transactional(readOnly = true)
public class CategoryValidationServiceImpl implements CategoryValidationService {

    private final CategoryRepository categoryRepository;

    public CategoryValidationServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void validateCreate(CreateCategoryRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Category name is required");
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("Category with name '" + request.getName() + "' already exists");
        }
    }

    @Override
    public void validateUpdate(Long id, UpdateCategoryRequest request) {
        if (request.getName() != null && request.getName().isBlank()) {
            throw new ValidationException("Category name cannot be empty");
        }

        // Kiểm tra tên đã được một danh mục khác sử dụng hay chưa.
        if (request.getName() != null) {
            boolean exists = categoryRepository.existsByNameAndIdNot(request.getName(), id);
            if (exists) {
                throw new BusinessException("Category name '" + request.getName() + "' is already in use");
            }
        }
    }

    @Override
    public void validateDelete(Long id) {
        // Kiểm tra danh mục hiện có đang được sách sử dụng hay không.
        boolean isInUse = categoryRepository.isInUse(id);
        if (isInUse) {
            throw new BusinessException("Cannot delete category that is in use by books");
        }
    }
}

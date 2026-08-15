package com.example.web_bansach.module.category.service.impl;

import org.springframework.cache.annotation.CacheEvict;
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
import com.example.web_bansach.common.cache.CacheNames;
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
    @CacheEvict(cacheNames = { CacheNames.CATEGORIES, CacheNames.BOOKS }, allEntries = true)
    public CategoryResponse create(CreateCategoryRequest request) {
        // Kiểm tra tính hợp lệ của dữ liệu tạo danh mục.
        validationService.validateCreate(request);

        // Chuyển dữ liệu yêu cầu thành thực thể danh mục.
        Category category = categoryMapper.toEntity(request);

        // Save to database
        Category saved = categoryRepository.save(category);

        return categoryMapper.toDto(saved);
    }

    @Override
    @CacheEvict(cacheNames = { CacheNames.CATEGORIES, CacheNames.BOOKS }, allEntries = true)
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        // Lấy danh mục hiện có.
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Kiểm tra tính hợp lệ của dữ liệu cập nhật danh mục.
        validationService.validateUpdate(id, request);

        // Cập nhật các trường dữ liệu.
        categoryMapper.updateEntity(request, category);

        // Save changes
        Category updated = categoryRepository.save(category);

        return categoryMapper.toDto(updated);
    }

    @Override
    @CacheEvict(cacheNames = { CacheNames.CATEGORIES, CacheNames.BOOKS }, allEntries = true)
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Kiểm tra điều kiện trước khi xóa danh mục.
        validationService.validateDelete(id);

        // Đánh dấu danh mục đã bị xóa theo cơ chế xóa mềm.
        category.softDelete();
        categoryRepository.save(category);
    }

    @Override
    @CacheEvict(cacheNames = { CacheNames.CATEGORIES, CacheNames.BOOKS }, allEntries = true)
    public void activate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setIsActive(true);
        categoryRepository.save(category);
    }

    @Override
    @CacheEvict(cacheNames = { CacheNames.CATEGORIES, CacheNames.BOOKS }, allEntries = true)
    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setIsActive(false);
        categoryRepository.save(category);
    }
}

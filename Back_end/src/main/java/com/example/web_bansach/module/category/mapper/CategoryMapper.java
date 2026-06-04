package com.example.web_bansach.module.category.mapper;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.entity.Category;

/**
 * Mapper for Category entity and DTOs.
 *
 * Keep the mapping logic small and explicit so it is easy to debug.
 */
@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category entity) {
        if (entity == null) {
            return null;
        }

        CategoryResponse dto = new CategoryResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public Category toEntity(CategoryResponse dto) {
        if (dto == null) {
            return null;
        }

        Category entity = new Category();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(dto.getIsActive());

        return entity;
    }

    /**
     * Convert create request to a new entity.
     */
    public Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category entity = new Category();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIsActive(true);

        return entity;
    }

    /**
     * Update an existing entity from request data.
     */
    public void updateEntity(UpdateCategoryRequest request, Category entity) {
        if (request == null) {
            return;
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName());
        }

        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
    }
}

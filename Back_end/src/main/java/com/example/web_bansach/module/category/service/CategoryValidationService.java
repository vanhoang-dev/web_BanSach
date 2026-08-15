package com.example.web_bansach.module.category.service;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;

/**
 * Giao diện dịch vụ kiểm tra dữ liệu danh mục.
 * Xử lý toàn bộ quy tắc nghiệp vụ cần kiểm tra.
 */
public interface CategoryValidationService {

    /**
     * Kiểm tra dữ liệu yêu cầu tạo danh mục.
     */
    void validateCreate(CreateCategoryRequest request);

    /**
     * Kiểm tra dữ liệu yêu cầu cập nhật danh mục.
     */
    void validateUpdate(Long id, UpdateCategoryRequest request);

    /**
     * Kiểm tra điều kiện cho phép xóa danh mục.
     */
    void validateDelete(Long id);
}

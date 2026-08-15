package com.example.web_bansach.module.category.service;

import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;

/**
 * Giao diện dịch vụ thực hiện các lệnh thay đổi danh mục.
 * Xử lý toàn bộ thao tác ghi dữ liệu gồm tạo, cập nhật và xóa danh mục.
 */
public interface CategoryCommandService {

    /**
     * Tạo danh mục mới.
     */
    CategoryResponse create(CreateCategoryRequest request);

    /**
     * Cập nhật danh mục hiện có.
     */
    CategoryResponse update(Long id, UpdateCategoryRequest request);

    /**
     * Xóa mềm danh mục theo mã định danh.
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

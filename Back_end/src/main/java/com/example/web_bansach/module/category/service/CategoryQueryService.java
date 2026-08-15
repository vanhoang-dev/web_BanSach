package com.example.web_bansach.module.category.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.category.dto.response.CategoryResponse;

/**
 * Giao diện dịch vụ truy vấn danh mục.
 * Xử lý toàn bộ thao tác đọc dữ liệu danh mục.
 */
public interface CategoryQueryService {

    /**
     * Lấy các danh mục đang hoạt động theo từng trang.
     */
    Page<CategoryResponse> getAllActive(int pageNumber, int pageSize);

    /**
     * Lấy danh mục theo mã định danh.
     */
    CategoryResponse getById(Long id);

    /**
     * Tìm danh mục theo tên.
     */
    Page<CategoryResponse> search(String keyword, int pageNumber, int pageSize);

    /**
     * Kiểm tra danh mục có tồn tại hay không.
     */
    boolean exists(Long id);
}

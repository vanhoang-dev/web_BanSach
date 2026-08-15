package com.example.web_bansach.module.category.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.service.CategoryQueryService;

/**
 * Bộ điều khiển danh mục dành cho khách và người dùng.
 * Xử lý các endpoint danh mục dành cho khách và người dùng.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryPublicController {

    private final CategoryQueryService queryService;

    public CategoryPublicController(CategoryQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/categories - Lấy các danh mục đang hoạt động, có phân trang.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<CategoryResponse> result = queryService.getAllActive(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    /**
     * GET /api/categories/{id} - Lấy danh mục theo mã định danh.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse result = queryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/categories/search?keyword=... - Tìm danh mục theo tên.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<CategoryResponse> result = queryService.search(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

}

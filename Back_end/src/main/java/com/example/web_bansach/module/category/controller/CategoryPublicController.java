package com.example.web_bansach.module.category.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.service.CategoryQueryService;

/**
 * Public Controller for Category
 * Handles public/user endpoints
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryPublicController {

    private final CategoryQueryService queryService;

    public CategoryPublicController(CategoryQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/categories - List all active categories with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<CategoryResponse> result = queryService.getAllActive(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    /**
     * GET /api/categories/{id} - Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse result = queryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/categories/search?keyword=... - Search categories by name
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

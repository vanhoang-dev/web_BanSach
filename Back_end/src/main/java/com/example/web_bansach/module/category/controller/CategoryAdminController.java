package com.example.web_bansach.module.category.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.category.dto.request.CreateCategoryRequest;
import com.example.web_bansach.module.category.dto.request.UpdateCategoryRequest;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.category.service.CategoryCommandService;
import com.example.web_bansach.module.category.service.CategoryQueryService;

import jakarta.validation.Valid;

/**
 * Admin Controller for Category
 * Handles admin-only endpoints for category management
 */
@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryAdminController {

    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;

    public CategoryAdminController(
            CategoryQueryService queryService,
            CategoryCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    /**
     * GET /api/admin/categories - List all categories (including inactive) with
     * pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<CategoryResponse> result = queryService.getAllActive(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    /**
     * POST /api/admin/categories - Create new category
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse result = commandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    /**
     * PUT /api/admin/categories/{id} - Update category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        CategoryResponse result = commandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * DELETE /api/admin/categories/{id} - Soft delete category
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        commandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    /**
     * PUT /api/admin/categories/{id}/activate - Activate category
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<?>> activate(@PathVariable Long id) {
        commandService.activate(id);
        return ResponseEntity.ok(ApiResponse.success("Category activated successfully", null));
    }

    /**
     * PUT /api/admin/categories/{id}/deactivate - Deactivate category
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivate(@PathVariable Long id) {
        commandService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Category deactivated successfully", null));
    }
}

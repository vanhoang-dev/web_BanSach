package com.example.web_bansach.module.author.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.module.author.service.AuthorQueryService;

@RestController
@RequestMapping("/api/authors")
// Cung cấp API công khai để người dùng xem, tìm kiếm và lọc sách theo tác giả.
public class AuthorPublicController {

    private final AuthorQueryService authorQueryService;

    // Khởi tạo controller với service truy vấn tác giả.
    public AuthorPublicController(AuthorQueryService authorQueryService) {
        this.authorQueryService = authorQueryService;
    }

    @GetMapping
    // Trả danh sách tác giả theo trang cho giao diện người dùng.
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> getAllAuthors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AuthorResponse> authors = authorQueryService.getAllAuthorPagination(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(authors)));
    }

    @GetMapping("/search")
    // Tìm tác giả theo từ khóa và trả kết quả có phân trang.
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> searchAuthors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AuthorResponse> authors = authorQueryService.searchAuthors(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(authors)));
    }

    @GetMapping("/{id}")
    // Trả thông tin chi tiết của một tác giả theo ID.
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorQueryService.getAuthorById(id)));
    }
}

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
public class AuthorPublicController {

    private final AuthorQueryService authorQueryService;

    public AuthorPublicController(AuthorQueryService authorQueryService) {
        this.authorQueryService = authorQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> getAllAuthors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AuthorResponse> authors = authorQueryService.getAllAuthorPagination(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(authors)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> searchAuthors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AuthorResponse> authors = authorQueryService.searchAuthors(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(authors)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorQueryService.getAuthorById(id)));
    }
}
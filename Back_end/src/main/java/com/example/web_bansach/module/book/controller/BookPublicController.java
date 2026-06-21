package com.example.web_bansach.module.book.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.service.BookQueryService;

@RestController
@RequestMapping("/user/books")
public class BookPublicController {

    private final BookQueryService bookQueryService;

    public BookPublicController(BookQueryService bookQueryService) {
        this.bookQueryService = bookQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getBooks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        if (authorId != null) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.from(bookQueryService.getBooksByAuthor(page, size, authorId, sortBy, sortDirection))));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.from(bookQueryService.searchBooks(page, size, keyword.trim(), sortBy, sortDirection))));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(ApiResponse.success(PageResponse.from(bookQueryService.getBooksByCategory(page, size, categoryId, sortBy, sortDirection))));
        }
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(bookQueryService.getAllBooks(page, size, sortBy, sortDirection))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookQueryService.getBookDetail(id)));
    }
}

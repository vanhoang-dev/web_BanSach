package com.example.web_bansach.module.book.controller;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.dto.response.BookAdminResponse;
import com.example.web_bansach.module.book.service.BookCommandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/books")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class BookAdminController {

    private final BookCommandService bookCommandService;

    public BookAdminController(BookCommandService bookCommandService) {
        this.bookCommandService = bookCommandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookAdminResponse>>> getBooksAdmin(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(bookCommandService.getAllBooks(pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookAdminResponse>> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookCommandService.getBookDetail(id)));
    }

    @PostMapping(value = "/create-book", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<BookAdminResponse>> createBook(
            @Valid @ModelAttribute BookRequest request,
            @RequestParam(required = false) MultipartFile image) throws Exception {
        return ResponseEntity.status(201).body(ApiResponse.created(bookCommandService.createBook(request, image)));
    }

    @PutMapping(value = "/update-book/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<BookAdminResponse>> updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute BookRequest request,
            @RequestParam(required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(ApiResponse.success(bookCommandService.updateBook(id, request, image)));
    }

    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBook(@PathVariable Long id) {
        bookCommandService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mềm sách thành công", null));
    }

}

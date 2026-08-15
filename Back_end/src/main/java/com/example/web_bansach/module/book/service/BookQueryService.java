package com.example.web_bansach.module.book.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.book.dto.response.BookResponse;

// Định nghĩa các truy vấn catalog, tìm kiếm, lọc và chi tiết sách.
public interface BookQueryService {

    Page<BookResponse> getAllBooks(Integer page, Integer size, String sortBy, String sortDirection);

    Page<BookResponse> getBooksByCategory(Integer page, Integer size, Long categoryId, String sortBy, String sortDirection);

    Page<BookResponse> getBooksByAuthor(Integer page, Integer size, Long authorId, String sortBy, String sortDirection);

    Page<BookResponse> searchBooks(Integer page, Integer size, String keyword, String sortBy, String sortDirection);

    BookResponse getBookDetail(Long id);
}

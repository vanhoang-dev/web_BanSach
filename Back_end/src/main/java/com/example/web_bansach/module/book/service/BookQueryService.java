package com.example.web_bansach.module.book.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.book.dto.response.BookResponse;

public interface BookQueryService {

    Page<BookResponse> getAllBooks(Integer page, Integer size);

    Page<BookResponse> getBooksByCategory(Integer page, Integer size, Long categoryId);

    Page<BookResponse> searchBooks(Integer page, Integer size, String keyword);

    BookResponse getBookDetail(Long id);
}
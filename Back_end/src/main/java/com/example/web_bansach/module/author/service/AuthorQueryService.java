package com.example.web_bansach.module.author.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.author.dto.response.AuthorResponse;

// Định nghĩa các truy vấn danh sách, tìm kiếm và chi tiết tác giả.
public interface AuthorQueryService {

    Page<AuthorResponse> getAllAuthorPagination(Integer page, Integer size);

    Page<AuthorResponse> searchAuthors(String keyword, Integer page, Integer size);

    AuthorResponse getAuthorById(Long id);
}

package com.example.web_bansach.module.author.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.author.dto.response.AuthorResponse;

public interface AuthorQueryService {

    Page<AuthorResponse> getAllAuthorPagination(Integer page, Integer size);

    Page<AuthorResponse> searchAuthors(String keyword, Integer page, Integer size);

    AuthorResponse getAuthorById(Long id);
}

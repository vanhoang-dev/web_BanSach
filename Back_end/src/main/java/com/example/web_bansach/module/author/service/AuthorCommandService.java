package com.example.web_bansach.module.author.service;

import com.example.web_bansach.module.author.dto.request.AuthorRequest;
import com.example.web_bansach.module.author.entity.Author;

// Định nghĩa các thao tác ghi dữ liệu tác giả cho tầng controller.
public interface AuthorCommandService {

    Author addAuthorService(AuthorRequest request);

    Author updateAuthorService(Long id, AuthorRequest request);

    void deleAuthorService(Long id);
}

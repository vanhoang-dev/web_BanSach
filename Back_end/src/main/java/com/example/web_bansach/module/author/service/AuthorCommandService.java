package com.example.web_bansach.module.author.service;

import com.example.web_bansach.module.author.dto.request.AuthorRequest;
import com.example.web_bansach.module.author.entity.Author;

public interface AuthorCommandService {

    Author addAuthorService(AuthorRequest request);

    Author updateAuthorService(Long id, AuthorRequest request);

    void deleAuthorService(Long id);
}

package com.example.web_bansach.module.author.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.author.service.AuthorQueryService;

@Service
public class AuthorQueryServiceImpl implements AuthorQueryService {

    private final AuthorRepository authorRepository;

    public AuthorQueryServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Page<AuthorResponse> getAllAuthorPagination(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Author> authorPage = authorRepository.findAll(pageable);

        return authorPage.map(author -> new AuthorResponse(
                author.getId(),
                author.getAuthorName(),
                author.getBiography()));
    }

    @Override
    public Page<AuthorResponse> searchAuthors(String keyword, Integer page, Integer size) {
        String searchKeyword = keyword == null ? "" : keyword.trim();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("authorName").ascending());
        Page<Author> authorPage = searchKeyword.isEmpty()
                ? authorRepository.findAll(pageable)
                : authorRepository.findByAuthorNameContainingIgnoreCase(searchKeyword, pageable);

        return authorPage.map(author -> new AuthorResponse(
                author.getId(),
                author.getAuthorName(),
                author.getBiography()));
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));
        return new AuthorResponse(author.getId(), author.getAuthorName(), author.getBiography());
    }
}
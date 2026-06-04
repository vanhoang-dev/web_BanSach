package com.example.web_bansach.module.author.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.author.dto.request.AuthorRequest;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.author.service.AuthorCommandService;

@Service
public class AuthorCommandServiceImpl implements AuthorCommandService {

    private final AuthorRepository authorRepository;

    public AuthorCommandServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author addAuthorService(AuthorRequest request) {
        String trimmedName = request.getAuthorName().trim();
        Author autCheck = authorRepository.findByAuthorName(trimmedName);
        if (autCheck != null) {
            throw new BusinessException("Tên tác giả đã tồn tại");
        }

        Author aut = new Author();
        aut.setAuthorName(trimmedName);
        aut.setBiography(request.getBiography().trim());
        return authorRepository.save(aut);
    }

    @Override
    public Author updateAuthorService(Long id, AuthorRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID tác giả không hợp lệ");
        }
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));

        author.setAuthorName(request.getAuthorName().trim());
        author.setBiography(request.getBiography().trim());
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleAuthorService(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả cần xóa"));
        authorRepository.deleteById(author.getId());
    }
}

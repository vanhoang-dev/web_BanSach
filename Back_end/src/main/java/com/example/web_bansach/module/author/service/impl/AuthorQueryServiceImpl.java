package com.example.web_bansach.module.author.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.cache.CacheNames;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.author.service.AuthorQueryService;

@Service
// Thực hiện các truy vấn đọc, tìm kiếm và phân trang tác giả.
@Transactional(readOnly = true)
public class AuthorQueryServiceImpl implements AuthorQueryService {

    private final AuthorRepository authorRepository;

    // Khởi tạo service với repository tác giả.
    public AuthorQueryServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    // Lấy một trang tác giả và chuyển entity sang response.
    @Cacheable(cacheNames = CacheNames.AUTHORS, key = "'page:' + #page + ':' + #size")
    public Page<AuthorResponse> getAllAuthorPagination(Integer page, Integer size) {
        validatePagination(page, size);
        PageRequest pageable = PageRequest.of(page, size);
        Page<Author> authorPage = authorRepository.findAll(pageable);

        return authorPage.map(author -> new AuthorResponse(
                author.getId(),
                author.getAuthorName(),
                author.getBiography()));
    }

    @Override
    // Tìm tác giả theo tên, đồng thời giữ metadata phân trang.
    @Cacheable(cacheNames = CacheNames.AUTHORS,
            key = "'search:' + (#keyword == null ? '' : #keyword.toLowerCase()) + ':' + #page + ':' + #size")
    public Page<AuthorResponse> searchAuthors(String keyword, Integer page, Integer size) {
        validatePagination(page, size);
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
    // Lấy chi tiết tác giả hoặc báo lỗi nếu ID không tồn tại.
    @Cacheable(cacheNames = CacheNames.AUTHORS, key = "'detail:' + #id")
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));
        return new AuthorResponse(author.getId(), author.getAuthorName(), author.getBiography());
    }

    // Bảo đảm số trang và kích thước trang nằm trong phạm vi hợp lệ.
    private void validatePagination(Integer page, Integer size) {
        if (page == null || page < 0 || size == null || size <= 0) {
            throw new BusinessException("Tham số phân trang không hợp lệ");
        }
    }
}

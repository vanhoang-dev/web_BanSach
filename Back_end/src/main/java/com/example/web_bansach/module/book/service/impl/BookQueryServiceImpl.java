package com.example.web_bansach.module.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.common.cache.CacheNames;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.service.BookQueryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
// Xử lý các truy vấn danh sách, tìm kiếm, lọc và chi tiết sách cho người dùng.
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    // Khởi tạo service với repository và mapper sách.
    public BookQueryServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional(readOnly = true)
    @Override
    // Lấy toàn bộ sách khả dụng theo trang và tiêu chí sắp xếp.
    @Cacheable(cacheNames = CacheNames.BOOKS,
            key = "'all:' + #page + ':' + #size + ':' + #sortBy + ':' + #sortDirection")
    public Page<BookResponse> getAllBooks(Integer page, Integer size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findAllActiveBooks(pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    // Lọc sách theo danh mục và trả kết quả có phân trang.
    @Cacheable(cacheNames = CacheNames.BOOKS,
            key = "'category:' + #categoryId + ':' + #page + ':' + #size + ':' + #sortBy + ':' + #sortDirection")
    public Page<BookResponse> getBooksByCategory(Integer page, Integer size, Long categoryId, String sortBy, String sortDirection) {
        log.info("Search books by category, categoryId={}, page={}, size={}", categoryId, page, size);
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findByCategoryIdWithJoin(categoryId, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    // Lọc sách theo tác giả và trả kết quả có phân trang.
    @Cacheable(cacheNames = CacheNames.BOOKS,
            key = "'author:' + #authorId + ':' + #page + ':' + #size + ':' + #sortBy + ':' + #sortDirection")
    public Page<BookResponse> getBooksByAuthor(Integer page, Integer size, Long authorId, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findByAuthorIdWithJoin(authorId, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    // Tìm sách theo từ khóa trong các trường mà repository hỗ trợ.
    @Cacheable(cacheNames = CacheNames.BOOKS,
            key = "'search:' + (#keyword == null ? '' : #keyword.toLowerCase()) + ':' + #page + ':' + #size + ':' + #sortBy + ':' + #sortDirection")
    public Page<BookResponse> searchBooks(Integer page, Integer size, String keyword, String sortBy, String sortDirection) {
        log.info("Search books by keyword, keyword={}, page={}, size={}", keyword, page, size);
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.searchByTitleWithJoin(keyword, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    // Lấy chi tiết sách đang khả dụng theo ID.
    @Cacheable(cacheNames = CacheNames.BOOKS, key = "'detail:' + #id")
    public BookResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdWithJoin(id);
        if (book == null || book.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return bookMapper.mapToResponseForUser(book);
    }

    // Chuẩn hóa tham số trang và tạo Pageable có sắp xếp an toàn.
    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size <= 0 ? 12 : size;
        String property = "price".equals(sortBy) ? "price" : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, property));
    }
}

package com.example.web_bansach.module.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.service.BookQueryService;

@Service
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookQueryServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> getAllBooks(Integer page, Integer size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findAllActiveBooks(pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> getBooksByCategory(Integer page, Integer size, Long categoryId, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findByCategoryIdWithJoin(categoryId, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> getBooksByAuthor(Integer page, Integer size, Long authorId, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.findByAuthorIdWithJoin(authorId, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> searchBooks(Integer page, Integer size, String keyword, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookRepository.searchByTitleWithJoin(keyword, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdWithJoin(id);
        if (book == null || book.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return bookMapper.mapToResponseForUser(book);
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size <= 0 ? 12 : size;
        String property = "price".equals(sortBy) ? "price" : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, property));
    }
}

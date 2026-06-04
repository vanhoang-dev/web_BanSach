package com.example.web_bansach.module.book.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.service.BookValidationService;

/**
 * Xử lý validation cho Book
 */
@Service
public class BookValidationServiceImpl implements BookValidationService {

    private final BookRepository bookRepository;

    public BookValidationServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public void validateCreateBook(BookRequest request) {
        validateBookFields(request);
        validateIsbnUnique(request.getIsbn().trim(), null);
    }

    @Transactional(readOnly = true)
    @Override
    public void validateUpdateBook(Long bookId, BookRequest request) {
        if (bookId == null || bookId <= 0) {
            throw new BusinessException("ID sách không hợp lệ");
        }

        validateBookFields(request);

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy sách"));

        String newIsbn = request.getIsbn().trim();
        String currentIsbn = existingBook.getIsbn() != null ? existingBook.getIsbn().trim() : null;
        if (currentIsbn == null || !currentIsbn.equals(newIsbn)) {
            validateIsbnUnique(newIsbn, bookId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void validateIsbnUnique(String isbn, Long excludeId) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new BusinessException("ISBN không được để trống");
        }

        boolean exists = excludeId != null
                ? bookRepository.existsByIsbnAndIdNot(isbn.trim(), excludeId)
                : bookRepository.existsByIsbn(isbn.trim());

        if (exists) {
            throw new BusinessException("ISBN đã tồn tại");
        }
    }

    private void validateBookFields(BookRequest request) {
        if (request == null) {
            throw new BusinessException("Thông tin sách không được để trống");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException("Tên sách không được để trống");
        }

        if (request.getIsbn() == null || request.getIsbn().trim().isEmpty()) {
            throw new BusinessException("ISBN không được để trống");
        }

        if (request.getAuthorId() == null || request.getAuthorId() <= 0) {
            throw new BusinessException("Tác giả không hợp lệ");
        }

        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            throw new BusinessException("Danh mục không hợp lệ");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Giá sách phải lớn hơn 0");
        }
    }
}

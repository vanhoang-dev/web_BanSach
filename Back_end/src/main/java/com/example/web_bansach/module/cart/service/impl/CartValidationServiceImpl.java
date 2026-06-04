package com.example.web_bansach.module.cart.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.service.CartValidationService;

/**
 * Xử lý validation cho Cart
 */
@Service
public class CartValidationServiceImpl implements CartValidationService {

    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    public CartValidationServiceImpl(BookRepository bookRepository,
            CartItemRepository cartItemRepository) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Số lượng phải lớn hơn 0");
        }

        if (quantity > 999) {
            throw new BusinessException("Số lượng không được vượt quá 999");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void validateBookExists(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new BusinessException("ID sách không hợp lệ");
        }

        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void validateCartItemExists(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException("ID sản phẩm trong giỏ không hợp lệ");
        }

        if (!cartItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
    }
}

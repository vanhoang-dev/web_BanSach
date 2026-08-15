package com.example.web_bansach.module.cart.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.service.impl.CartValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartValidationServiceImplTest {

    @Mock private BookRepository bookRepository;
    @Mock private CartItemRepository cartItemRepository;

    @Test
    void validateBookExists_shouldRejectNullZeroAndNegativeIds() {
        CartValidationServiceImpl validationService = new CartValidationServiceImpl(bookRepository, cartItemRepository);

        assertThatThrownBy(() -> validationService.validateBookExists(null))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> validationService.validateBookExists(0L))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> validationService.validateBookExists(-10L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateBookExists_shouldRejectMissingBook() {
        CartValidationServiceImpl validationService = new CartValidationServiceImpl(bookRepository, cartItemRepository);
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateBookExists(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateQuantity_shouldRejectNullZeroNegativeAndTooLargeValues() {
        CartValidationServiceImpl validationService = new CartValidationServiceImpl(bookRepository, cartItemRepository);

        assertThatThrownBy(() -> validationService.validateQuantity(null))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> validationService.validateQuantity(0))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> validationService.validateQuantity(-1))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> validationService.validateQuantity(1000))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateQuantity_shouldAcceptBusinessBoundaryValues() {
        CartValidationServiceImpl validationService = new CartValidationServiceImpl(bookRepository, cartItemRepository);

        assertThatCode(() -> validationService.validateQuantity(1)).doesNotThrowAnyException();
        assertThatCode(() -> validationService.validateQuantity(999)).doesNotThrowAnyException();
    }
}

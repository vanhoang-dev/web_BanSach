package com.example.web_bansach.module.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.impl.CartQueryServiceImpl;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartQueryServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartQueryServiceImpl cartQueryService;

    @Test
    void getCart_shouldReturnEmptyCartWithoutWritingInsideReadOnlyTransaction() {
        Users user = new Users();
        user.setId(1L);
        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        var response = cartQueryService.getCart("user@test.com");

        assertThat(response.getCartId()).isNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalItems()).isZero();
        assertThat(response.getTotalAmount()).isZero();
        verify(cartRepository, never()).save(org.mockito.ArgumentMatchers.any(Cart.class));
    }
}

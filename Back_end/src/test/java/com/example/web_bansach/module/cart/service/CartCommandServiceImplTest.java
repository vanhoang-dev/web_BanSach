package com.example.web_bansach.module.cart.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.mapper.CartItemMapper;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.impl.CartCommandServiceImpl;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.pricing.service.PricingService;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartCommandServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private PricingService pricingService;
    @Mock private CartItemMapper cartItemMapper;
    @Mock private CartValidationService cartValidationService;

    @InjectMocks
    private CartCommandServiceImpl cartCommandService;

    @Test
    void updateCartItem_shouldRejectQuantityGreaterThanInventory() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Book book = new Book();
        book.setId(100L);

        CartItem item = new CartItem();
        item.setId(20L);
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(1);

        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(2);

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(20L)).thenReturn(Optional.of(item));
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> cartCommandService.updateCartItem("user@test.com", 20L, 3))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("không đủ");

        verify(cartItemRepository, never()).save(item);
    }
}

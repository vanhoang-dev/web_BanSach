package com.example.web_bansach.module.order.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
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
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.request.BuyNowOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.voucher.service.VoucherService;

@ExtendWith(MockitoExtension.class)
class OrderUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private BookRepository bookRepository;
    @Mock private VoucherService voucherService;
    @Mock private OrderMapper orderMapper;
    @Mock private RealtimeNotificationService realtimeNotificationService;
    @Mock private OrderValidationService orderValidationService;

    @InjectMocks
    private OrderUserService orderUserService;

    @Test
    void createOrder_shouldRejectInvalidVoucherAndNotCreateOrder() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Book book = new Book();
        book.setId(100L);
        book.setTitle("Book");

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(1);
        item.setPrice(new BigDecimal("100000"));

        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(5);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setReceiverName("Nguyen Van A");
        request.setReceiverPhone("0901234567");
        request.setShippingAddress("Ha Noi");
        request.setShippingFee(BigDecimal.ZERO);
        request.setVoucherCode("BAD");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdWithBook(10L)).thenReturn(List.of(item));
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(inventory));
        when(voucherService.getMyVoucherByCode("user@test.com", "BAD"))
                .thenThrow(new BusinessException("Mã voucher không thuộc tài khoản hoặc không hợp lệ"));

        assertThatThrownBy(() -> orderUserService.createOrder("user@test.com", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("voucher");

        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any(Order.class));
    }

    @Test
    void buyNow_shouldCreatePendingOrderWithoutDecreasingInventoryBeforePayment() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("user001");
        user.setEmail("user@test.com");

        Book book = new Book();
        book.setId(100L);
        book.setTitle("Book");
        book.setPrice(new BigDecimal("5000"));

        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(5);

        BuyNowOrderRequest request = new BuyNowOrderRequest();
        request.setBookId(100L);
        request.setQuantity(2);
        request.setReceiverName("Nguyen Van A");
        request.setReceiverPhone("0901234567");
        request.setShippingAddress("Ha Noi");
        request.setShippingFee(BigDecimal.ZERO);

        OrderResponse mappedResponse = new OrderResponse();
        mappedResponse.setId(99L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(bookRepository.findById(100L)).thenReturn(Optional.of(book));
        when(inventoryRepository.findByBookIdForUpdate(100L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });
        when(orderMapper.mapToResponse(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(mappedResponse);

        OrderResponse response = orderUserService.buyNow("user@test.com", request);

        org.assertj.core.api.Assertions.assertThat(response.getId()).isEqualTo(99L);
        org.assertj.core.api.Assertions.assertThat(inventory.getQuantity()).isEqualTo(5);
        verify(orderItemRepository).save(org.mockito.ArgumentMatchers.any());
        verify(cartItemRepository, never()).deleteByCartId(org.mockito.ArgumentMatchers.anyLong());
    }
}

package com.example.web_bansach.module.cart.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.CartQueryService;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@Service
public class CartQueryServiceImpl implements CartQueryService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartQueryServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            CartResponse empty = new CartResponse();
            empty.setTotalItems(0);
            empty.setCartId(null);
            empty.setItems(List.of());
            empty.setTotalAmount(BigDecimal.ZERO);
            return empty;
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setCreatedAt(LocalDateTime.now());
                    c.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(c);
                });

        List<CartItem> items = cartItemRepository.findByCartIdWithBook(cart.getId());
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(items.size());
        List<CartItemResponse> itemResponses = items.stream()
                .map(item -> {
                    CartItemResponse resp = new CartItemResponse();
                    resp.setId(item.getId());
                    resp.setBookId(item.getBook().getId());
                    resp.setBookTitle(item.getBook().getTitle());
                    resp.setBookCoverImage(item.getBook().getCoverImage());
                    resp.setBookPrice(item.getBook().getPrice());
                    BigDecimal priceAfterDiscount = calculatePrice(item.getBook());
                    Integer discountPercent = (item.getBook().getDiscount() != null
                            && item.getBook().getDiscount().getIsActive())
                                    ? item.getBook().getDiscount().getDiscountPercent()
                                    : null;
                    resp.setDiscountPercent(discountPercent);
                    resp.setPriceAfterDiscount(priceAfterDiscount);
                    resp.setQuantity(item.getQuantity());
                    BigDecimal subtotal = priceAfterDiscount.multiply(new BigDecimal(item.getQuantity()));
                    resp.setSubtotal(subtotal);
                    return resp;
                })
                .collect(Collectors.toList());
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);
        return response;
    }

    private BigDecimal calculatePrice(com.example.web_bansach.module.book.entity.Book book) {
        BigDecimal price = book.getPrice();
        if (book.getDiscount() != null && book.getDiscount().getIsActive()) {
            BigDecimal discountPercent = new BigDecimal(book.getDiscount().getDiscountPercent());
            BigDecimal discountAmount = price.multiply(discountPercent).divide(new BigDecimal(100));
            price = price.subtract(discountAmount);
        }
        return price;
    }
}
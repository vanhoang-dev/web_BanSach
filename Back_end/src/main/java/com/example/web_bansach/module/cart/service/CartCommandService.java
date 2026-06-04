package com.example.web_bansach.module.cart.service;

import com.example.web_bansach.module.cart.dto.request.AddToCartRequest;
import com.example.web_bansach.module.cart.dto.response.CartResponse;

public interface CartCommandService {

    CartResponse addToCart(String username, AddToCartRequest request);

    CartResponse updateCartItem(String username, Long itemId, Integer quantity);

    CartResponse removeCartItem(String username, Long itemId);

    void clearCart(String username);
}

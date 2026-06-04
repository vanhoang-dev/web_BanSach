package com.example.web_bansach.module.cart.service;

import com.example.web_bansach.module.cart.dto.response.CartResponse;

public interface CartQueryService {

    CartResponse getCart(String username);

}

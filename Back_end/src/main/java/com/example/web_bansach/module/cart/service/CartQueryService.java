package com.example.web_bansach.module.cart.service;

import com.example.web_bansach.module.cart.dto.response.CartResponse;

// Định nghĩa thao tác đọc giỏ hàng đã tính đầy đủ giá tiền.
public interface CartQueryService {

    CartResponse getCart(String username);

}

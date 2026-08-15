package com.example.web_bansach.module.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
// Trả toàn bộ dòng sản phẩm cùng tổng số lượng và tổng tiền của giỏ.
public class CartResponse {
    private Long cartId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
}

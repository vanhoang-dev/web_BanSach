package com.example.web_bansach.module.cart.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
// Trả thông tin sách, giá sau giảm, số lượng và thành tiền của một dòng giỏ.
public class CartItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookCoverImage;
    private BigDecimal bookPrice;
    private Integer discountPercent;
    private BigDecimal priceAfterDiscount;
    private Integer quantity;
    private BigDecimal subtotal;
}

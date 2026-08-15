package com.example.web_bansach.module.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
// Trả sách, ảnh, đơn giá và số lượng của một dòng đơn hàng.
public class OrderItemResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long bookId;
    private String bookTitle;
    private String bookCoverImage;
    private Integer quantity;
    private BigDecimal price;
}

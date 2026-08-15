package com.example.web_bansach.module.book.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
// Trả thông tin sách cần thiết cho giao diện khách hàng.
public class BookResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private String authorName;
    private String categoryName;
    private Integer discountPercent;
}

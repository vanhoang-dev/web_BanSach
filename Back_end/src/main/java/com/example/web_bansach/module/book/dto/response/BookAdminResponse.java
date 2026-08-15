package com.example.web_bansach.module.book.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
// Trả dữ liệu sách mở rộng phục vụ biểu mẫu và bảng quản trị.
public class BookAdminResponse {
    private Long id;
    private String title;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private Long discountId;
    private Integer discountPercent;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}

package com.example.web_bansach.module.wishlist.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
// Trả thông tin sách đã được người dùng lưu vào danh sách yêu thích.
public class WishlistResponse {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverImage;
    private String bookDescription;
    private String bookPublisher;
    private java.math.BigDecimal bookPrice;
    private LocalDateTime addedAt;
}

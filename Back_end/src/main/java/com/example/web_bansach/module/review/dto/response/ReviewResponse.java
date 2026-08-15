package com.example.web_bansach.module.review.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
// Trả nội dung đánh giá cùng thông tin người viết và sách.
public class ReviewResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

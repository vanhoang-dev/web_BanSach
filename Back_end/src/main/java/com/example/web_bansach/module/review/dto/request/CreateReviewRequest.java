package com.example.web_bansach.module.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// Chứa số sao và nội dung người dùng gửi khi đánh giá sách.
public class CreateReviewRequest {

    @NotNull(message = "ID sách không được để trống")
    private Long bookId;

    @NotNull(message = "Đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá phải từ 1 đến 5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1 đến 5 sao")
    private Integer rating; // 1-5 stars

    @Size(max = 1000, message = "Bình luận tối đa 1000 ký tự")
    private String comment;
}

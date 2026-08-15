package com.example.web_bansach.module.book.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
// Chứa và kiểm tra dữ liệu admin gửi khi tạo hoặc cập nhật sách.
public class BookRequest {
    @NotBlank(message = "Tên sách không được để trống")
    private String title;

    @NotBlank(message = "ISBN không được để trống")
    private String isbn;

    @NotBlank(message = "Nhà xuất bản không được để trống")
    private String publisher;

    @NotNull(message = "Năm xuất bản không được để trống")
    private Integer publicationYear;

    @NotNull(message = "Giá sách không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Giá sách phải lớn hơn 0")
    private BigDecimal price;

    private String description;

    @NotNull(message = "Tác giả không được để trống")
    @Positive(message = "ID tác giả không hợp lệ")
    private Long authorId;

    @NotNull(message = "Danh mục không được để trống")
    @Positive(message = "ID danh mục không hợp lệ")
    private Long categoryId;

    private Long discountId;
}

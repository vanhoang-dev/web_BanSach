package com.example.web_bansach.module.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// Chứa bookId và số lượng mà người dùng muốn thêm vào giỏ.
public class AddToCartRequest {
    @NotNull(message = "Book ID không được để trống")
    @Min(value = 1, message = "Book ID phai lon hon 0")
    private Long bookId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;
}

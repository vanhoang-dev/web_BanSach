package com.example.web_bansach.module.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// Chứa số lượng mới của một dòng sản phẩm trong giỏ.
public class UpdateCartItemRequest {
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;
}

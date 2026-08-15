package com.example.web_bansach.module.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
// Mở rộng thông tin giao hàng với bookId và số lượng cho luồng mua ngay.
public class BuyNowOrderRequest extends CreateOrderRequest {

    @NotNull(message = "ID sách không được để trống")
    @Min(value = 1, message = "ID sach phai lon hon 0")
    private Long bookId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}

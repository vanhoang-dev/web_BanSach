package com.example.web_bansach.module.order.dto.request;

import com.example.web_bansach.module.order.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// Chứa trạng thái mới mà admin muốn áp dụng cho đơn hàng.
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;
}

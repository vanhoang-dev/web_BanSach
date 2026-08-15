package com.example.web_bansach.module.order.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// Chứa thông tin nhận hàng, vận chuyển và voucher khi tạo đơn.
public class CreateOrderRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 255, message = "Tên người nhận tối đa 255 ký tự")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "^(0|\\+84)([0-9]{9})$", message = "Số điện thoại không hợp lệ")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    @Size(max = 500, message = "Địa chỉ nhận hàng tối đa 500 ký tự")
    private String shippingAddress;

    @Size(max = 100, message = "Phương thức giao hàng tối đa 100 ký tự")
    private String shippingMethod;

    @DecimalMin(value = "0", inclusive = true, message = "Phí vận chuyển phải >= 0")
    private BigDecimal shippingFee;

    @Size(max = 50, message = "Mã voucher tối đa 50 ký tự")
    private String voucherCode; // Mã voucher (optional)
}

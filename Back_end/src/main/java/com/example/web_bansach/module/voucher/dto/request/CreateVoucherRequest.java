package com.example.web_bansach.module.voucher.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// Chứa mã, phần trăm giảm, giới hạn, số lượng và hạn dùng của voucher.
public class CreateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 3, max = 50, message = "Mã voucher phải từ 3-50 ký tự")
    private String code;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá tối thiểu 1%")
    @Max(value = 100, message = "Phần trăm giảm giá tối đa 100%")
    private Integer discountPercent;

    @NotNull(message = "Số tiền giảm tối đa không được để trống")
    @DecimalMin(value = "0", inclusive = true, message = "Số tiền giảm phải >= 0")
    private BigDecimal maxDiscount;

    @NotNull(message = "Số lượng voucher không được để trống")
    @Min(value = 1, message = "Số lượng voucher tối thiểu 1")
    private Integer quantity;

    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDate expiredAt;
}

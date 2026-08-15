package com.example.web_bansach.module.voucher.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
// Trả thông tin voucher và trạng thái còn hiệu lực cho frontend.
public class VoucherResponse {
    private Long id;
    private String code;
    private Integer discountPercent;
    private BigDecimal maxDiscount;
    private Integer quantity;
    private Integer usedQuantity;
    private LocalDate expiredAt;
    private Boolean isExpired;
    private Boolean isValid;
}

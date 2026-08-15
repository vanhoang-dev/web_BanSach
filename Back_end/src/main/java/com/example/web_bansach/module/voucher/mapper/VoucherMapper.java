package com.example.web_bansach.module.voucher.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.entity.Voucher;

/**
 * Chuyển thực thể voucher thành dữ liệu phản hồi.
 */
@Component
public class VoucherMapper {

    /**
     * Chuyển một thực thể voucher thành dữ liệu phản hồi.
     */
    public VoucherResponse mapToResponse(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setCode(voucher.getCode());
        response.setDiscountPercent(voucher.getDiscountPercent());
        response.setMaxDiscount(voucher.getMaxDiscount());
        response.setQuantity(voucher.getQuantity());
        response.setExpiredAt(voucher.getExpiredAt());

        // Kiểm tra thời hạn của voucher.
        LocalDate today = LocalDate.now();
        boolean isExpired = voucher.getExpiredAt().isBefore(today);
        response.setIsExpired(isExpired);

        // Kiểm tra voucher còn hạn và vẫn còn số lượng sử dụng.
        boolean isValid = !isExpired && voucher.getQuantity() > 0;
        response.setIsValid(isValid);

        return response;
    }
}

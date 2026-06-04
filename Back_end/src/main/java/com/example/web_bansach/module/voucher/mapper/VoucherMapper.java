package com.example.web_bansach.module.voucher.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.entity.Voucher;

/**
 * Mapper xử lý mapping Voucher entity sang VoucherResponse
 */
@Component
public class VoucherMapper {

    /**
     * Map Voucher entity sang VoucherResponse
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

        // Check expiration
        LocalDate today = LocalDate.now();
        boolean isExpired = voucher.getExpiredAt().isBefore(today);
        response.setIsExpired(isExpired);

        // Check if valid (not expired and still has quantity)
        boolean isValid = !isExpired && voucher.getQuantity() > 0;
        response.setIsValid(isValid);

        return response;
    }
}

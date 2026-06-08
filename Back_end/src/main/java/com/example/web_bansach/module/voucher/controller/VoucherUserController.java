package com.example.web_bansach.module.voucher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.service.VoucherService;

/**
 * Controller xem voucher dành cho user
 */
@RestController
@RequestMapping("/user/vouchers")
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
public class VoucherUserController {

    @Autowired
    private VoucherService voucherService;

    /**
     * Lấy danh sách voucher hợp lệ
     * GET /user/vouchers?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VoucherResponse>>> getValidVouchers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VoucherResponse> vouchers = voucherService.getValidVouchers(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(vouchers)));
    }

    /**
     * Lấy chi tiết voucher bằng mã
     * GET /user/vouchers/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherByCode(@PathVariable String code) {
        VoucherResponse voucher = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(ApiResponse.success(voucher));
    }
}

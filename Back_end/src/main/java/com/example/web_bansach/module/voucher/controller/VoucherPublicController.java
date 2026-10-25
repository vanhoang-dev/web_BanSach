package com.example.web_bansach.module.voucher.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.service.VoucherService;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherPublicController {

    private final VoucherService voucherService;

    public VoucherPublicController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VoucherResponse>>> getValidVouchers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        Page<VoucherResponse> vouchers = voucherService.getValidVouchers(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(vouchers)));
    }
}

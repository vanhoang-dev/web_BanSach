package com.example.web_bansach.module.voucher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.voucher.dto.request.CreateVoucherRequest;
import com.example.web_bansach.module.voucher.dto.response.VoucherResponse;
import com.example.web_bansach.module.voucher.service.VoucherService;

import jakarta.validation.Valid;

/**
 * Controller quản lý voucher (admin)
 */
@RestController
@RequestMapping("/admin/vouchers")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class VoucherAdminController {

    @Autowired
    private VoucherService voucherService;

    /**
     * Tạo voucher mới
     * POST /admin/vouchers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
        VoucherResponse voucher = voucherService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(voucher));
    }

    /**
     * Cập nhật voucher
     * PUT /admin/vouchers/{voucherId}
     */
    @PutMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucher(
            @PathVariable Long voucherId,
            @Valid @RequestBody CreateVoucherRequest request) {
        VoucherResponse voucher = voucherService.updateVoucher(voucherId, request);
        return ResponseEntity.ok(ApiResponse.success(voucher));
    }

    /**
     * Xóa voucher
     * DELETE /admin/vouchers/{voucherId}
     */
    @DeleteMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<?>> deleteVoucher(@PathVariable Long voucherId) {
        voucherService.deleteVoucher(voucherId);
        return ResponseEntity.ok(ApiResponse.success("Voucher đã được xóa thành công", null));
    }

    /**
     * Lấy chi tiết voucher
     * GET /admin/vouchers/{voucherId}
     */
    @GetMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherDetail(@PathVariable Long voucherId) {
        VoucherResponse voucher = voucherService.getVoucherDetail(voucherId);
        return ResponseEntity.ok(ApiResponse.success(voucher));
    }

    /**
     * Lấy danh sách tất cả voucher
     * GET /admin/vouchers?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VoucherResponse>>> getAllVouchers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VoucherResponse> vouchers = voucherService.getAllVouchers(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(vouchers)));
    }

    /**
     * Lấy danh sách voucher hết hạn
     * GET /admin/vouchers/expired?page=0&size=10
     */
    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<PageResponse<VoucherResponse>>> getExpiredVouchers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VoucherResponse> vouchers = voucherService.getExpiredVouchers(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(vouchers)));
    }
}

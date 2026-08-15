package com.example.web_bansach.module.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.dashboard.dto.AdminDashboardResponse;
import com.example.web_bansach.module.dashboard.service.AdminDashboardService;

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
// Cung cấp số liệu tổng hợp phục vụ bảng điều khiển quản trị.
public class AdminDashboardController {
    private final AdminDashboardService dashboardService;

    // Khởi tạo controller với service tổng hợp thống kê.
    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    // Trả các chỉ số và đơn hàng gần đây cho trang dashboard admin.
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard()));
    }
}

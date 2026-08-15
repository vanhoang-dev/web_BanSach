package com.example.web_bansach.module.order.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.order.dto.request.UpdateOrderStatusRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/orders")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
// Cung cấp API quản trị để theo dõi và cập nhật trạng thái mọi đơn hàng.
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    // Trả danh sách đơn hàng toàn hệ thống theo trang.
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(orderService.getAllOrders(page, size))));
    }

    @GetMapping("/{id}")
    // Trả toàn bộ chi tiết của một đơn hàng cho admin.
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetail(id)));
    }

    @PutMapping("/{id}/status")
    // Chuyển đơn hàng sang trạng thái xử lý mới hợp lệ.
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderStatus(id, request.getStatus())));
    }

    @PutMapping("/{id}/cancel")
    // Hủy đơn hàng từ màn hình quản trị.
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng thành công", null));
    }
}

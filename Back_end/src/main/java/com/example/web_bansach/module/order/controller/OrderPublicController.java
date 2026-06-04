package com.example.web_bansach.module.order.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.service.OrderUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/orders")
@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
public class OrderPublicController {

    @Autowired
    private OrderUserService orderUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(orderUserService.createOrder(username, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(orderUserService.getMyOrders(username, page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderDetail(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(orderUserService.getMyOrderDetail(username, id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelMyOrder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        orderUserService.cancelMyOrder(username, id);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng thành công", null));
    }
}

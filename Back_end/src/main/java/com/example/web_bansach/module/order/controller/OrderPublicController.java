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

import com.example.web_bansach.common.logging.LogMaskingUtil;
import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.order.dto.request.BuyNowOrderRequest;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.service.OrderUserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/orders")
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
@Slf4j
// Cung cấp API tạo, xem và hủy đơn hàng thuộc tài khoản đang đăng nhập.
public class OrderPublicController {

    @Autowired
    private OrderUserService orderUserService;

    @PostMapping
    // Tạo đơn hàng từ toàn bộ sản phẩm hiện có trong giỏ.
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(orderUserService.createOrder(username, request)));
        } catch (RuntimeException ex) {
            log.warn("Create order failed, email={}", LogMaskingUtil.maskEmail(username), ex);
            throw ex;
        }
    }

    @PostMapping("/buy-now")
    // Tạo đơn mua ngay cho một cuốn sách mà không cần đưa vào giỏ.
    public ResponseEntity<ApiResponse<OrderResponse>> buyNow(@Valid @RequestBody BuyNowOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(orderUserService.buyNow(username, request)));
        } catch (RuntimeException ex) {
            log.warn("Create buy-now order failed, email={}, bookId={}",
                    LogMaskingUtil.maskEmail(username),
                    request.getBookId(),
                    ex);
            throw ex;
        }
    }

    @GetMapping
    // Trả lịch sử đơn hàng của người dùng theo trang.
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(orderUserService.getMyOrders(username, page, size))));
    }

    @GetMapping("/{id}")
    // Trả chi tiết đơn hàng sau khi xác minh quyền sở hữu.
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderDetail(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(orderUserService.getMyOrderDetail(username, id)));
    }

    @PutMapping("/{id}/cancel")
    // Hủy đơn của người dùng nếu trạng thái hiện tại còn cho phép.
    public ResponseEntity<ApiResponse<?>> cancelMyOrder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        orderUserService.cancelMyOrder(username, id);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng thành công", null));
    }
}

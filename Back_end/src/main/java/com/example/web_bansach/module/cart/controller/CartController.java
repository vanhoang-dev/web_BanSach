package com.example.web_bansach.module.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.cart.dto.request.AddToCartRequest;
import com.example.web_bansach.module.cart.dto.request.UpdateCartItemRequest;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.service.CartCommandService;
import com.example.web_bansach.module.cart.service.CartQueryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user/cart")
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
// Cung cấp API để người dùng xem và thay đổi giỏ hàng của chính mình.
public class CartController {

    private final CartCommandService cartCommandService;

    private final CartQueryService cartQueryService;

    // Khởi tạo controller với service đọc và service cập nhật giỏ hàng.
    public CartController(CartCommandService cartCommandService, CartQueryService cartQueryService) {
        this.cartCommandService = cartCommandService;
        this.cartQueryService = cartQueryService;
    }

    @GetMapping
    // Trả giỏ hàng hiện tại cùng tổng số lượng và tổng tiền.
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(cartQueryService.getCart(username)));
    }

    @PostMapping("/items")
    // Thêm sách vào giỏ hoặc tăng số lượng nếu sách đã tồn tại.
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.addToCart(username, request)));
    }

    @PutMapping("/items/{itemId}")
    // Cập nhật số lượng của một dòng sản phẩm trong giỏ.
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.updateCartItem(username, itemId, request.getQuantity())));
    }

    @DeleteMapping("/items/{itemId}")
    // Xóa một dòng sản phẩm khỏi giỏ hàng.
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(cartCommandService.removeCartItem(username, itemId)));
    }

    @DeleteMapping("/clear")
    // Xóa toàn bộ sản phẩm trong giỏ của người dùng hiện tại.
    public ResponseEntity<ApiResponse<?>> clearCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartCommandService.clearCart(username);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa toàn bộ giỏ hàng", null));
    }
}

package com.example.web_bansach.module.wishlist.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.service.WishlistCommandService;
import com.example.web_bansach.module.wishlist.service.WishlistQueryService;

/**
 * Controller xử lý danh sách yêu thích (wishlist)
 */
@RestController
@RequestMapping("/user/wishlist")
@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
public class WishlistController {

    private final WishlistCommandService wishlistCommandService;

    private final WishlistQueryService wishlistQueryService;

    private final UserRepository userRepository;

    public WishlistController(WishlistCommandService wishlistCommandService,
            WishlistQueryService wishlistQueryService,
            UserRepository userRepository) {
        this.wishlistCommandService = wishlistCommandService;
        this.wishlistQueryService = wishlistQueryService;
        this.userRepository = userRepository;
    }

    /**
     * Thêm sách vào danh sách yêu thích
     * POST /user/wishlist/books/{bookId}
     */
    @PostMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        WishlistResponse wishlist = wishlistCommandService.addToWishlist(username, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(wishlist));
    }

    /**
     * Xóa sách khỏi danh sách yêu thích
     * DELETE /user/wishlist/books/{bookId}
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<?>> removeFromWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        wishlistCommandService.removeFromWishlist(username, bookId);
        return ResponseEntity.ok(ApiResponse.success("Sách đã được xóa khỏi danh sách yêu thích", null));
    }

    /**
     * Kiểm tra sách có trong danh sách yêu thích không
     * GET /user/wishlist/books/{bookId}/check
     */
    @GetMapping("/books/{bookId}/check")
    public ResponseEntity<ApiResponse<?>> checkInWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        boolean isInWishlist = wishlistQueryService.isInWishlist(username, bookId);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("isInWishlist", isInWishlist)));
    }

    /**
     * Lấy danh sách yêu thích của user
     * GET /user/wishlist?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WishlistResponse>>> getMyWishlist(
            Authentication auth,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        String username = auth.getName();
        Page<WishlistResponse> wishlist = wishlistQueryService.getMyWishlist(username, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(wishlist)));
    }

    /**
     * Lấy số lượng sách trong danh sách yêu thích
     * GET /user/wishlist/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getWishlistCount(Authentication auth) {
        String username = auth.getName();
        long count = wishlistQueryService.getWishlistCount(username);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("count", count)));
    }

    /**
     * Xóa toàn bộ danh sách yêu thích của user hiện tại
     * DELETE /user/wishlist/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<?>> clearWishlist(Authentication auth) {
        String username = auth.getName();
        Long userId = userRepository.findByUsername(username).getId();
        wishlistCommandService.clearWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa toàn bộ danh sách yêu thích", null));
    }
}

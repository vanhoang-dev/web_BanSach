package com.example.web_bansach.module.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.service.ReviewService;

import jakarta.validation.Valid;

/**
 * Bộ điều khiển đánh giá sách dành cho người dùng.
 */
@RestController
@RequestMapping("/user/reviews")
@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
public class ReviewUserController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Tạo đánh giá mới
     * POST /user/reviews
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            Authentication auth,
            @Valid @RequestBody CreateReviewRequest request) {
        String username = auth.getName();
        ReviewResponse review = reviewService.createReview(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(review));
    }

    /**
     * Cập nhật đánh giá của chính mình
     * PUT /user/reviews/{reviewId}
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            Authentication auth,
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewRequest request) {
        String username = auth.getName();
        ReviewResponse review = reviewService.updateReview(username, reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    /**
     * Xóa đánh giá của chính mình
     * DELETE /user/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(
            Authentication auth,
            @PathVariable Long reviewId) {
        String username = auth.getName();
        reviewService.deleteReview(username, reviewId);
        return ResponseEntity.ok(ApiResponse.success("Đánh giá đã được xóa thành công", null));
    }

    /**
     * Lấy đánh giá của người dùng dành cho một cuốn sách.
     * GET /user/reviews/book/{bookId}/my-review
     */
    @GetMapping("/book/{bookId}/my-review")
    public ResponseEntity<ApiResponse<ReviewResponse>> getMyReview(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        ReviewResponse review = reviewService.getMyReview(username, bookId);
        if (review == null) {
            return ResponseEntity.ok(ApiResponse.failure(HttpStatus.NOT_FOUND.value(), "Bạn chưa đánh giá sách này"));
        }
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    /**
     * Lấy tất cả đánh giá của một sách
     * GET /user/reviews/book/{bookId}?page=0&size=10
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReviewResponse> reviews = reviewService.getReviewsByBook(bookId, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(reviews)));
    }

    /**
     * Lấy thống kê rating của sách
     * GET /user/reviews/book/{bookId}/stats
     */
    @GetMapping("/book/{bookId}/stats")
    public ResponseEntity<ApiResponse<?>> getReviewStats(@PathVariable Long bookId) {
        Double avgRating = reviewService.getAverageRating(bookId);
        long reviewCount = reviewService.getReviewCount(bookId);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of(
                "averageRating", avgRating != null ? avgRating : 0.0,
                "reviewCount", reviewCount
        )));
    }
}

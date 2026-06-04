package com.example.web_bansach.module.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.service.ReviewService;

/**
 * Controller quản lý đánh giá sách (admin)
 */
@RestController
@RequestMapping("/admin/reviews")
@PreAuthorize("hasAuthority('ADMIN')")
public class ReviewAdminController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Lấy tất cả đánh giá của một user
     * GET /admin/reviews/user/{userId}?page=0&size=10
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReviewResponse> reviews = reviewService.getReviewsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(reviews)));
    }

    /**
     * Lấy chi tiết một đánh giá
     * GET /admin/reviews/{reviewId}
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponse review = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    /**
     * Xóa đánh giá (admin)
     * DELETE /admin/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewAdmin(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Đánh giá đã được xóa thành công", null));
    }
}

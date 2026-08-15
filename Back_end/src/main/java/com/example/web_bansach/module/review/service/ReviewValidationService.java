package com.example.web_bansach.module.review.service;

import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;

/**
 * Dịch vụ kiểm tra dữ liệu đánh giá sách.
 */
public interface ReviewValidationService {

    /**
     * Kiểm tra dữ liệu yêu cầu đánh giá sách.
     * 
     * @param request dữ liệu tạo đánh giá
     * @throws BusinessException nếu không hợp lệ
     */
    void validateReviewRequest(CreateReviewRequest request);

    /**
     * Kiểm tra giá trị điểm đánh giá.
     * 
     * @param rating - Điểm rating
     * @throws BusinessException nếu không hợp lệ
     */
    void validateRating(Integer rating);
}

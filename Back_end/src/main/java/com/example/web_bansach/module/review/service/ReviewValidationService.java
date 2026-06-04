package com.example.web_bansach.module.review.service;

import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;

/**
 * Service xử lý validation cho Review
 */
public interface ReviewValidationService {

    /**
     * Validate review request
     * 
     * @param request - CreateReviewRequest
     * @throws BusinessException nếu không hợp lệ
     */
    void validateReviewRequest(CreateReviewRequest request);

    /**
     * Validate rating value
     * 
     * @param rating - Điểm rating
     * @throws BusinessException nếu không hợp lệ
     */
    void validateRating(Integer rating);
}

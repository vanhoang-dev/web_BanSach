package com.example.web_bansach.module.review.service.impl;

import org.springframework.stereotype.Service;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.service.ReviewValidationService;

/**
 * Xử lý validation cho Review
 */
@Service
public class ReviewValidationServiceImpl implements ReviewValidationService {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    @Override
    public void validateReviewRequest(CreateReviewRequest request) {
        if (request == null) {
            throw new BusinessException("Thông tin đánh giá không hợp lệ");
        }

        if (request.getBookId() == null || request.getBookId() <= 0) {
            throw new BusinessException("ID sách không hợp lệ");
        }

        validateRating(request.getRating());
    }

    @Override
    public void validateRating(Integer rating) {
        if (rating == null) {
            throw new BusinessException("Điểm rating không được để trống");
        }

        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new BusinessException("Điểm rating phải từ " + MIN_RATING + " đến " + MAX_RATING);
        }
    }
}

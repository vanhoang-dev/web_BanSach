package com.example.web_bansach.module.review.mapper;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.entity.Review;

/**
 * Mapper xử lý mapping Review entity sang ReviewResponse
 */
@Component
public class ReviewMapper {

    /**
     * Map Review entity sang ReviewResponse
     */
    public ReviewResponse mapToResponse(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());

        if (review.getUser() != null) {
            response.setUserId(review.getUser().getId());
            response.setUserName(review.getUser().getFullName() != null
                    ? review.getUser().getFullName()
                    : review.getUser().getUsername());
        }

        if (review.getBook() != null) {
            response.setBookId(review.getBook().getId());
            response.setBookTitle(review.getBook().getTitle());
        }

        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }
}

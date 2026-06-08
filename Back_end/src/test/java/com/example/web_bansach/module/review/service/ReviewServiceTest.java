package com.example.web_bansach.module.review.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.mapper.ReviewMapper;
import com.example.web_bansach.module.review.repository.ReviewRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private ReviewValidationService reviewValidationService;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReview_shouldRejectWhenUserHasNotCompletedPurchase() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("user@test.com");

        Book book = new Book();
        book.setId(2L);

        CreateReviewRequest request = new CreateReviewRequest();
        request.setBookId(2L);
        request.setRating(5);
        request.setComment("Good");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(orderRepository.countCompletedItemsByUserIdAndBookId(1L, 2L)).thenReturn(0L);

        assertThatThrownBy(() -> reviewService.createReview("user@test.com", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("sau khi");
    }
}

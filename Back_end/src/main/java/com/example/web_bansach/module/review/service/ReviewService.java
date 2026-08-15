package com.example.web_bansach.module.review.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.cache.CacheNames;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.entity.Review;
import com.example.web_bansach.module.review.mapper.ReviewMapper;
import com.example.web_bansach.module.review.repository.ReviewRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

/**
 * Dịch vụ xử lý nghiệp vụ đánh giá sách.
 * Nhận các thành phần phụ thuộc thông qua hàm khởi tạo.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewValidationService reviewValidationService;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            ReviewMapper reviewMapper,
            ReviewValidationService reviewValidationService,
            OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
        this.reviewValidationService = reviewValidationService;
        this.orderRepository = orderRepository;
    }

    /**
     * Tạo đánh giá mới cho người dùng.
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.BOOK_REVIEWS, allEntries = true)
    public ReviewResponse createReview(String username, CreateReviewRequest request) {
        reviewValidationService.validateReviewRequest(request);

        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        validatePurchasedBook(user.getId(), book.getId());

        // Kiểm tra người dùng đã đánh giá sách này chưa.
        if (reviewRepository.findByUserIdAndBookId(user.getId(), book.getId()).isPresent()) {
            throw new BusinessException("Bạn đã đánh giá sách này rồi");
        }

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.mapToResponse(savedReview);
    }

    /**
     * Cho phép người dùng cập nhật đánh giá của chính mình.
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.BOOK_REVIEWS, allEntries = true)
    public ReviewResponse updateReview(String username, Long reviewId, CreateReviewRequest request) {
        reviewValidationService.validateReviewRequest(request);

        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra đánh giá có thuộc người dùng này không.
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Bạn không có quyền sửa đánh giá này");
        }

        if (!review.getBook().getId().equals(request.getBookId())) {
            throw new BusinessException("Không thể thay đổi sách của đánh giá");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.mapToResponse(updatedReview);
    }

    /**
     * Cho phép người dùng xóa đánh giá của chính mình.
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.BOOK_REVIEWS, allEntries = true)
    public void deleteReview(String username, Long reviewId) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra đánh giá có thuộc người dùng này không.
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }

    private void validatePurchasedBook(Long userId, Long bookId) {
        long purchasedCount = orderRepository.countCompletedItemsByUserIdAndBookId(userId, bookId);
        if (purchasedCount <= 0) {
            throw new BusinessException("Chỉ có thể đánh giá sau khi đã mua và hoàn thành đơn hàng");
        }
    }

    /**
     * Lấy đánh giá của người dùng dành cho một cuốn sách.
     */
    @Transactional(readOnly = true)
    public ReviewResponse getMyReview(String username, Long bookId) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElse(null);

        return review != null ? reviewMapper.mapToResponse(review) : null;
    }

    /**
     * Lấy toàn bộ đánh giá của một cuốn sách cho người dùng.
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.BOOK_REVIEWS, key = "'list:' + #bookId + ':' + #page + ':' + #size")
    public Page<ReviewResponse> getReviewsByBook(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByBookId(bookId, pageable)
                .map(reviewMapper::mapToResponse);
    }

    /**
     * Lấy toàn bộ đánh giá của một người dùng cho quản trị viên.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUserId(userId, pageable)
                .map(reviewMapper::mapToResponse);
    }

    /**
     * Lấy chi tiết một đánh giá
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findByIdWithJoin(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        return reviewMapper.mapToResponse(review);
    }

    /**
     * Cho phép quản trị viên xóa đánh giá.
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.BOOK_REVIEWS, allEntries = true)
    public void deleteReviewAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        reviewRepository.delete(review);
    }

    /**
     * Lấy rating trung bình của sách
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.BOOK_REVIEWS, key = "'average:' + #bookId")
    public Double getAverageRating(Long bookId) {
        return reviewRepository.getAverageRatingByBookId(bookId);
    }

    /**
     * Lấy số lượng đánh giá của sách
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.BOOK_REVIEWS, key = "'count:' + #bookId")
    public long getReviewCount(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }
}

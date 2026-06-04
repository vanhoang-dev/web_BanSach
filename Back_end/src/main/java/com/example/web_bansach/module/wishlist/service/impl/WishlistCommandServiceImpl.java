package com.example.web_bansach.module.wishlist.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.entity.Wishlist;
import com.example.web_bansach.module.wishlist.entity.WishlistId;
import com.example.web_bansach.module.wishlist.mapper.WishlistMapper;
import com.example.web_bansach.module.wishlist.repository.WishlistRepository;
import com.example.web_bansach.module.wishlist.service.WishlistCommandService;

/**
 * Service quản lý danh sách yêu thích
 */
@Service
public class WishlistCommandServiceImpl implements WishlistCommandService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistCommandServiceImpl(WishlistRepository wishlistRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.wishlistMapper = wishlistMapper;
    }

    /**
     * Thêm sách vào danh sách yêu thích
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WishlistResponse addToWishlist(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Kiểm tra sách đã bị xóa chưa
        if (book.getDeletedAt() != null) {
            throw new BusinessException("Sách này không còn khả dụng");
        }

        // Kiểm tra sách đã trong wishlist chưa
        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BusinessException("Sách này đã có trong danh sách yêu thích của bạn");
        }

        WishlistId id = new WishlistId(user.getId(), book.getId());
        Wishlist wishlist = new Wishlist();
        wishlist.setId(id);
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setCreatedAt(LocalDateTime.now());

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.mapToResponse(savedWishlist);
    }

    /**
     * Xóa sách khỏi danh sách yêu thích
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeFromWishlist(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        if (!wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new BusinessException("Sách này không có trong danh sách yêu thích");
        }

        wishlistRepository.deleteByIdUserIdAndIdBookId(user.getId(), bookId);
    }

    /**
     * Xóa toàn bộ danh sách yêu thích (admin - khi xóa user)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void clearWishlist(Long userId) {
        var wishlists = wishlistRepository.findAllByIdUserId(userId);
        wishlistRepository.deleteAll(wishlists);
    }

}
package com.example.web_bansach.module.wishlist.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.mapper.WishlistMapper;
import com.example.web_bansach.module.wishlist.repository.WishlistRepository;
import com.example.web_bansach.module.wishlist.service.WishlistQueryService;

/**
 * Service quản lý danh sách yêu thích
 */
@Service
public class WishlistQueryServiceImpl implements WishlistQueryService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistQueryServiceImpl(WishlistRepository wishlistRepository,
            UserRepository userRepository,
            WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.wishlistMapper = wishlistMapper;
    }

    /**
     * Kiểm tra sách có trong danh sách yêu thích không
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isInWishlist(String username, Long bookId) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            return false;
        }
        return wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId);
    }

    /**
     * Lấy danh sách yêu thích có phân trang của người dùng.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<WishlistResponse> getMyWishlist(String username, int page, int size) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wishlistRepository.findByIdUserId(user.getId(), pageable)
                .map(wishlistMapper::mapToResponse);
    }

    /**
     * Lấy số lượng sách trong danh sách yêu thích
     */
    @Transactional(readOnly = true)
    @Override
    public long getWishlistCount(String username) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        return wishlistRepository.countByUserId(user.getId());
    }
}

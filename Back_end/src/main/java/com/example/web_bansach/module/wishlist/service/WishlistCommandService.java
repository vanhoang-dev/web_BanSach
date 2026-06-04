package com.example.web_bansach.module.wishlist.service;

import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;

public interface WishlistCommandService {

    WishlistResponse addToWishlist(String username, Long bookId);

    void removeFromWishlist(String username, Long bookId);

    void clearWishlist(Long userId);
}
package com.example.web_bansach.module.wishlist.service;

import org.springframework.data.domain.Page;

import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;

public interface WishlistQueryService {

    boolean isInWishlist(String username, Long bookId);

    Page<WishlistResponse> getMyWishlist(String username, int page, int size);

    long getWishlistCount(String username);
}
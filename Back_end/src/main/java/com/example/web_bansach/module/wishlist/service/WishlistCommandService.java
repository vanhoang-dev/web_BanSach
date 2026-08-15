package com.example.web_bansach.module.wishlist.service;

import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;

// Định nghĩa thao tác thêm, xóa và làm rỗng danh sách yêu thích.
public interface WishlistCommandService {

    WishlistResponse addToWishlist(String username, Long bookId);

    void removeFromWishlist(String username, Long bookId);

    void clearWishlist(Long userId);
}

package com.example.web_bansach.module.wishlist.mapper;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.entity.Wishlist;

/**
 * Mapper xử lý mapping Wishlist entity sang WishlistResponse
 */
@Component
public class WishlistMapper {

    /**
     * Map Wishlist entity sang WishlistResponse
     */
    public WishlistResponse mapToResponse(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistResponse response = new WishlistResponse();
        if (wishlist.getBook() != null) {
            Book book = wishlist.getBook();
            response.setBookId(book.getId());
            response.setBookTitle(book.getTitle());
            response.setBookCoverImage(book.getCoverImage());
            response.setBookDescription(book.getDescription());
            response.setBookPublisher(book.getPublisher());
            response.setBookPrice(book.getPrice());

            if (book.getAuthor() != null) {
                response.setBookAuthor(book.getAuthor().getAuthorName());
            }
        }

        response.setAddedAt(wishlist.getCreatedAt());
        return response;
    }
}
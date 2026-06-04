package com.example.web_bansach.module.book.mapper;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.book.dto.response.BookAdminResponse;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.entity.Book;

@Component
public class BookMapper {

    public BookAdminResponse mapToAdminResponse(Book book) {
        BookAdminResponse response = new BookAdminResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setPublicationYear(book.getPublicationYear());
        response.setPrice(book.getPrice());
        response.setDescription(book.getDescription());
        response.setCoverImage(book.getCoverImage());
        response.setAuthorId(book.getAuthor() != null ? book.getAuthor().getId() : null);
        response.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : null);
        response.setCategoryId(book.getCategory() != null ? book.getCategory().getId() : null);
        response.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : null);
        response.setDiscountId(book.getDiscount() != null ? book.getDiscount().getId() : null);
        response.setDiscountPercent(book.getDiscount() != null ? book.getDiscount().getDiscountPercent() : null);
        response.setCreatedAt(book.getCreatedAt());
        response.setDeletedAt(book.getDeletedAt());
        return response;
    }

    public BookResponse mapToResponseForUser(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setPrice(book.getPrice());
        response.setDescription(book.getDescription());
        response.setCoverImage(book.getCoverImage());
        response.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : null);
        response.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : null);
        response.setDiscountPercent(book.getDiscount() != null ? book.getDiscount().getDiscountPercent() : null);
        return response;
    }
}

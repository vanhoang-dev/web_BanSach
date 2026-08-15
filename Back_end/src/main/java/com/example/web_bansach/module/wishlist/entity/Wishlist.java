package com.example.web_bansach.module.wishlist.entity;

import java.time.LocalDateTime;

import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wishlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Ánh xạ quan hệ yêu thích giữa người dùng và sách.
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

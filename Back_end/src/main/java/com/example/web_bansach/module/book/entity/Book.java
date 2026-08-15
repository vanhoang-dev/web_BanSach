package com.example.web_bansach.module.book.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.category.entity.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Ánh xạ sách cùng tác giả, danh mục, giảm giá và ảnh bìa trong database.
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;
}

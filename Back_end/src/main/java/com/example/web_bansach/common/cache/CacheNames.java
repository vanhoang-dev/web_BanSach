package com.example.web_bansach.common.cache;

// Tập trung tên các vùng cache để tránh viết sai chuỗi ở nhiều service.
public final class CacheNames {

    public static final String AUTHORS = "authors";
    public static final String CATEGORIES = "categories";
    public static final String BOOKS = "books";
    public static final String BOOK_REVIEWS = "book-reviews";
    public static final String DASHBOARD = "dashboard";

    // Không cho tạo đối tượng vì class này chỉ chứa hằng số.
    private CacheNames() {
    }
}

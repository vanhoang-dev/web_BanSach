package com.example.web_bansach.common.constant;

/**
 * Application-level constants
 */
public class AppConstants {

    private AppConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Default pagination
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Các giới hạn áp dụng khi tải tệp lên hệ thống.
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String UPLOAD_DIR = "books";

    // Currency
    public static final String CURRENCY_CODE = "VND";

    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Giá trị dùng cho cơ chế xóa mềm dữ liệu.
    public static final String SOFT_DELETE_FLAG = "deletedAt";
}

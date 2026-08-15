package com.example.web_bansach.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thông tin phân trang được sử dụng cùng dữ liệu phản hồi theo trang.
 *
 * Số trang bắt đầu từ 0 để thống nhất với quy ước của Spring Data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public boolean hasNextPage() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPreviousPage() {
        return pageNumber > 0;
    }

    public boolean isFirstPage() {
        return pageNumber == 0;
    }

    public boolean isLastPage() {
        return pageNumber >= totalPages - 1;
    }
}

package com.example.web_bansach.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page metadata used together with PageResponse.
 *
 * pageNumber is zero-based because Spring Data pages start from 0.
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

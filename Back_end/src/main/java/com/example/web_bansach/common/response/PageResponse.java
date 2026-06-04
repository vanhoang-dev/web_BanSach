package com.example.web_bansach.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper for paged API data.
 *
 * content: items on the current page
 * meta: page information needed by the frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private PaginationMeta meta;

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.content = content;
        this.meta = new PaginationMeta(pageNumber, pageSize, totalElements, totalPages);
    }

    public static <T> PageResponse<T> of(List<T> content, PaginationMeta meta) {
        return new PageResponse<>(content, meta);
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        if (page == null) {
            return new PageResponse<>();
        }

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}

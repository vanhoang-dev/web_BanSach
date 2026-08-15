package com.example.web_bansach.common.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.category.dto.response.CategoryResponse;
import com.example.web_bansach.module.dashboard.dto.AdminDashboardResponse;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;

class RedisCacheSerializationTest {

    @Test
    // Bảo đảm các DTO và dữ liệu phân trang có thể được Redis ghi bằng bộ tuần tự mặc định.
    void cachedResponsesCanBeSerialized() {
        assertDoesNotThrow(() -> {
            try (ObjectOutputStream output = new ObjectOutputStream(OutputStream.nullOutputStream())) {
                output.writeObject(new PageImpl<>(List.of(new AuthorResponse()), PageRequest.of(0, 10), 1));
                output.writeObject(new PageImpl<>(List.of(new CategoryResponse()), PageRequest.of(0, 10), 1));
                output.writeObject(new PageImpl<>(List.of(new BookResponse()), PageRequest.of(0, 10), 1));
                output.writeObject(new PageImpl<>(List.of(new ReviewResponse()), PageRequest.of(0, 10), 1));
                output.writeObject(new AdminDashboardResponse());
            }
        });
    }
}

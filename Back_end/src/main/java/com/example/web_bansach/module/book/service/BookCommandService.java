package com.example.web_bansach.module.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.dto.response.BookAdminResponse;

// Định nghĩa các lệnh quản trị tạo, sửa, xóa và đọc sách.
public interface BookCommandService {

    BookAdminResponse createBook(BookRequest request, MultipartFile image) throws Exception;

    BookAdminResponse updateBook(Long id, BookRequest request, MultipartFile image) throws Exception;

    void deleteBook(Long id);

    BookAdminResponse getBookDetail(Long id);

    Page<BookAdminResponse> getAllBooks(Pageable pageable);
}

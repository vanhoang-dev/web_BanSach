package com.example.web_bansach.module.book.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.entity.Book;

/**
 * Service xử lý assembly/construction của Book entity
 * Tách riêng để tuân thủ Single Responsibility Principle
 */
public interface BookAssemblyService {

    /**
     * Tạo entity Book từ request
     * 
     * @param request   - BookRequest chứa thông tin sách
     * @param imageFile - file hình ảnh (có thể null)
     * @return Book entity đã được xây dựng
     * @throws Exception nếu có lỗi
     */
    Book assembleBookFromRequest(BookRequest request, MultipartFile imageFile) throws Exception;

    /**
     * Cập nhật entity Book từ request
     * 
     * @param book      - Book entity hiện tại
     * @param request   - BookRequest chứa thông tin mới
     * @param imageFile - file hình ảnh mới (có thể null)
     * @return Book entity đã được cập nhật
     * @throws Exception nếu có lỗi
     */
    Book updateBookFromRequest(Book book, BookRequest request, MultipartFile imageFile) throws Exception;
}

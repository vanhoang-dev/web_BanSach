package com.example.web_bansach.module.book.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.entity.Book;

/**
 * Dịch vụ xây dựng và cập nhật thực thể sách.
 * Tách riêng để tuân thủ Single Responsibility Principle
 */
public interface BookAssemblyService {

    /**
     * Tạo thực thể sách từ dữ liệu yêu cầu.
     * 
     * @param request dữ liệu thông tin sách
     * @param imageFile - file hình ảnh (có thể null)
     * @return thực thể sách đã được xây dựng
     * @throws Exception nếu có lỗi
     */
    Book assembleBookFromRequest(BookRequest request, MultipartFile imageFile) throws Exception;

    /**
     * Cập nhật thực thể sách từ dữ liệu yêu cầu.
     * 
     * @param book thực thể sách hiện tại
     * @param request dữ liệu mới của sách
     * @param imageFile - file hình ảnh mới (có thể null)
     * @return thực thể sách đã được cập nhật
     * @throws Exception nếu có lỗi
     */
    Book updateBookFromRequest(Book book, BookRequest request, MultipartFile imageFile) throws Exception;
}

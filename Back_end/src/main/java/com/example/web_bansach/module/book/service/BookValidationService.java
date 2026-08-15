package com.example.web_bansach.module.book.service;

import com.example.web_bansach.module.book.dto.request.BookRequest;

/**
 * Dịch vụ kiểm tra dữ liệu sách.
 * Tách riêng để tuân thủ Single Responsibility Principle
 */
public interface BookValidationService {

    /**
     * Kiểm tra thông tin khi tạo sách mới.
     * 
     * @param request dữ liệu của sách mới
     * @throws BusinessException nếu validation thất bại
     */
    void validateCreateBook(BookRequest request);

    /**
     * Kiểm tra thông tin khi cập nhật sách.
     * 
     * @param bookId mã sách cần cập nhật
     * @param request dữ liệu mới của sách
     * @throws BusinessException nếu validation thất bại
     */
    void validateUpdateBook(Long bookId, BookRequest request);

    /**
     * Kiểm tra mã ISBN không bị trùng lặp.
     * 
     * @param isbn mã ISBN cần kiểm tra
     * @param excludeId mã sách cần loại khỏi phép kiểm tra khi cập nhật
     * @throws BusinessException nếu ISBN đã tồn tại
     */
    void validateIsbnUnique(String isbn, Long excludeId);
}

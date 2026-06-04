package com.example.web_bansach.module.book.service;

import com.example.web_bansach.module.book.dto.request.BookRequest;

/**
 * Service xử lý validation cho Book
 * Tách riêng để tuân thủ Single Responsibility Principle
 */
public interface BookValidationService {

    /**
     * Validate thông tin khi tạo sách mới
     * 
     * @param request - BookRequest chứa thông tin sách mới
     * @throws BusinessException nếu validation thất bại
     */
    void validateCreateBook(BookRequest request);

    /**
     * Validate thông tin khi cập nhật sách
     * 
     * @param bookId  - ID của sách cần update
     * @param request - BookRequest chứa thông tin mới
     * @throws BusinessException nếu validation thất bại
     */
    void validateUpdateBook(Long bookId, BookRequest request);

    /**
     * Validate ISBN unique
     * 
     * @param isbn      - ISBN cần check
     * @param excludeId - ID sách để exclude khỏi check (dùng khi update)
     * @throws BusinessException nếu ISBN đã tồn tại
     */
    void validateIsbnUnique(String isbn, Long excludeId);
}

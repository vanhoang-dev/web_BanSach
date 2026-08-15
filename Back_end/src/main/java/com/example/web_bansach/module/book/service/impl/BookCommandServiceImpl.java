package com.example.web_bansach.module.book.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.common.cache.CacheNames;
import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.dto.response.BookAdminResponse;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.service.BookAssemblyService;
import com.example.web_bansach.module.book.service.BookCommandService;
import com.example.web_bansach.module.book.service.BookValidationService;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Dịch vụ xử lý nghiệp vụ sách dành cho quản trị viên.
 * Sử dụng composition để tuân thủ Single Responsibility Principle:
 * - BookValidationService: xử lý validation
 * - BookAssemblyService: xử lý xây dựng thực thể sách.
 * - BookMapper: xử lý mapping
 */
@Service
@Slf4j
// Điều phối việc tạo, cập nhật, xóa và đọc dữ liệu sách dành cho quản trị viên.
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookValidationService bookValidationService;
    private final BookAssemblyService bookAssemblyService;
    private final InventoryRepository inventoryRepository;

    // Khởi tạo service với repository và các service lắp ráp/kiểm tra sách.
    public BookCommandServiceImpl(BookRepository bookRepository,
            BookMapper bookMapper,
            BookValidationService bookValidationService,
            BookAssemblyService bookAssemblyService,
            InventoryRepository inventoryRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookValidationService = bookValidationService;
        this.bookAssemblyService = bookAssemblyService;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Thêm sách mới (Admin)
     * Transaction rollback nếu có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    // Kiểm tra yêu cầu, tải ảnh lên, lưu sách và tạo bản ghi tồn kho ban đầu.
    @CacheEvict(cacheNames = { CacheNames.BOOKS, CacheNames.DASHBOARD }, allEntries = true)
    public BookAdminResponse createBook(BookRequest request, MultipartFile image) throws Exception {
        log.info("Create book started, categoryId={}", request.getCategoryId());
        // Validation
        bookValidationService.validateCreateBook(request);

        // Xây dựng thực thể sách.
        Book book = bookAssemblyService.assembleBookFromRequest(request, image);

        // Save
        Book savedBook = bookRepository.save(book);
        createEmptyInventory(savedBook);
        log.info("Create book successfully, bookId={}, categoryId={}",
                savedBook.getId(),
                savedBook.getCategory() != null ? savedBook.getCategory().getId() : null);

        // Chuyển sách vừa lưu thành dữ liệu phản hồi.
        return bookMapper.mapToAdminResponse(savedBook);
    }

    /**
     * Cập nhật sách (Admin)
     * Transaction rollback nếu có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    // Cập nhật thông tin sách và thay ảnh bìa khi quản trị viên gửi ảnh mới.
    @CacheEvict(cacheNames = { CacheNames.BOOKS, CacheNames.DASHBOARD }, allEntries = true)
    public BookAdminResponse updateBook(Long id, BookRequest request, MultipartFile image) throws Exception {
        log.info("Update book started, bookId={}, categoryId={}", id, request.getCategoryId());
        // Kiểm tra sách có tồn tại hay không.
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Validation
        bookValidationService.validateUpdateBook(id, request);

        // Cập nhật thực thể sách.
        book = bookAssemblyService.updateBookFromRequest(book, request, image);

        // Save
        Book updatedBook = bookRepository.save(book);
        log.info("Update book successfully, bookId={}, categoryId={}",
                updatedBook.getId(),
                updatedBook.getCategory() != null ? updatedBook.getCategory().getId() : null);

        // Chuyển sách vừa cập nhật thành dữ liệu phản hồi.
        return bookMapper.mapToAdminResponse(updatedBook);
    }

    /**
     * Xóa mềm sách (Admin)
     */
    @Transactional
    @Override
    // Xóa mềm sách để giữ nguyên tham chiếu trong đơn hàng lịch sử.
    @CacheEvict(cacheNames = { CacheNames.BOOKS, CacheNames.DASHBOARD }, allEntries = true)
    public void deleteBook(Long id) {
        log.info("Delete book started, bookId={}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        book.setDeletedAt(LocalDateTime.now());
        bookRepository.save(book);
        log.info("Delete book successfully, bookId={}", id);
    }

    /**
     * Lấy chi tiết sách (Admin)
     */
    @Transactional(readOnly = true)
    @Override
    // Trả chi tiết sách theo định dạng quản trị.
    public BookAdminResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdWithJoin(id);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return bookMapper.mapToAdminResponse(book);
    }

    /**
     * Lấy danh sách sách (Admin)
     */
    @Transactional(readOnly = true)
    @Override
    // Trả toàn bộ sách, kể cả dữ liệu cần quản trị, theo Pageable.
    public Page<BookAdminResponse> getAllBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAllActiveBooks(pageable);
        return page.map(bookMapper::mapToAdminResponse);
    }

    // Tạo tồn kho bằng 0 ngay khi sách mới được thêm vào hệ thống.
    private void createEmptyInventory(Book book) {
        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);
    }
}

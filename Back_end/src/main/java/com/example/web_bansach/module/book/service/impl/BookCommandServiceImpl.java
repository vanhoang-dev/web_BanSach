package com.example.web_bansach.module.book.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
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

/**
 * Service xử lý nghiệp vụ Book cho Admin
 * Sử dụng composition để tuân thủ Single Responsibility Principle:
 * - BookValidationService: xử lý validation
 * - BookAssemblyService: xử lý construction entity
 * - BookMapper: xử lý mapping
 */
@Service
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookValidationService bookValidationService;
    private final BookAssemblyService bookAssemblyService;
    private final InventoryRepository inventoryRepository;

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
    public BookAdminResponse createBook(BookRequest request, MultipartFile image) throws Exception {
        // Validation
        bookValidationService.validateCreateBook(request);

        // Assembly entity
        Book book = bookAssemblyService.assembleBookFromRequest(request, image);

        // Save
        Book savedBook = bookRepository.save(book);
        createEmptyInventory(savedBook);

        // Map response
        return bookMapper.mapToAdminResponse(savedBook);
    }

    /**
     * Cập nhật sách (Admin)
     * Transaction rollback nếu có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BookAdminResponse updateBook(Long id, BookRequest request, MultipartFile image) throws Exception {
        // Check exist
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Validation
        bookValidationService.validateUpdateBook(id, request);

        // Update entity
        book = bookAssemblyService.updateBookFromRequest(book, request, image);

        // Save
        Book updatedBook = bookRepository.save(book);

        // Map response
        return bookMapper.mapToAdminResponse(updatedBook);
    }

    /**
     * Xóa mềm sách (Admin)
     */
    @Transactional
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        book.setDeletedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    /**
     * Lấy chi tiết sách (Admin)
     */
    @Transactional(readOnly = true)
    @Override
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
    public Page<BookAdminResponse> getAllBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAllActiveBooks(pageable);
        return page.map(bookMapper::mapToAdminResponse);
    }

    private void createEmptyInventory(Book book) {
        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);
    }
}

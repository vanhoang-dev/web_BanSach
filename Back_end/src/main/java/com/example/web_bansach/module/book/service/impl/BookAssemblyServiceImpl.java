package com.example.web_bansach.module.book.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.infrastructure.file.FileUploadService;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.entity.Discount;
import com.example.web_bansach.module.book.repository.DiscountRepository;
import com.example.web_bansach.module.book.service.BookAssemblyService;
import com.example.web_bansach.module.category.entity.Category;
import com.example.web_bansach.module.category.repository.CategoryRepository;

/**
 * Xử lý assembly/construction của Book entity
 */
@Service
public class BookAssemblyServiceImpl implements BookAssemblyService {

    private final FileUploadService fileUploadService;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;

    public BookAssemblyServiceImpl(FileUploadService fileUploadService,
            AuthorRepository authorRepository,
            CategoryRepository categoryRepository,
            DiscountRepository discountRepository) {
        this.fileUploadService = fileUploadService;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Book assembleBookFromRequest(BookRequest request, MultipartFile imageFile) throws Exception {
        // Load các entity liên quan
        Author author = loadAuthor(request.getAuthorId());
        Category category = loadCategory(request.getCategoryId());
        Discount discount = loadDiscount(request.getDiscountId());

        // Upload image nếu có
        String imageUrl = uploadImageIfProvided(imageFile);

        // Tạo Book entity
        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn().trim());
        book.setPublisher(request.getPublisher().trim());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setCoverImage(imageUrl);
        book.setAuthor(author);
        book.setCategory(category);
        book.setDiscount(discount);
        book.setCreatedAt(LocalDateTime.now());

        return book;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Book updateBookFromRequest(Book book, BookRequest request, MultipartFile imageFile) throws Exception {
        // Load các entity liên quan
        Author author = loadAuthor(request.getAuthorId());
        Category category = loadCategory(request.getCategoryId());
        Discount discount = loadDiscount(request.getDiscountId());

        // Update image nếu có file mới
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileUploadService.uploadFile(imageFile, "books");
            book.setCoverImage(imageUrl);
        }

        // Update thông tin sách
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn().trim());
        book.setPublisher(request.getPublisher().trim());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setAuthor(author);
        book.setCategory(category);
        book.setDiscount(discount);

        return book;
    }

    private Author loadAuthor(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));
    }

    private Category loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    }

    private Discount loadDiscount(Long discountId) {
        if (discountId == null) {
            return null;
        }
        return discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình giảm giá"));
    }

    private String uploadImageIfProvided(MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }
        return fileUploadService.uploadFile(imageFile, "books");
    }
}

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
 * Xử lý việc xây dựng và cập nhật thực thể sách.
 */
@Service
// Lắp ráp thực thể sách từ yêu cầu và liên kết tác giả, danh mục, giảm giá, ảnh bìa.
public class BookAssemblyServiceImpl implements BookAssemblyService {

    private final FileUploadService fileUploadService;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;

    // Khởi tạo service với kho file và repository của các quan hệ sách.
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
    // Tạo thực thể sách mới hoàn chỉnh từ yêu cầu và ảnh tải lên.
    public Book assembleBookFromRequest(BookRequest request, MultipartFile imageFile) throws Exception {
        // Tải các thực thể liên quan.
        Author author = loadAuthor(request.getAuthorId());
        Category category = loadCategory(request.getCategoryId());
        Discount discount = loadDiscount(request.getDiscountId());

        // Upload image nếu có
        String imageUrl = uploadImageIfProvided(imageFile);

        // Tạo thực thể sách.
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
    // Ghi dữ liệu yêu cầu vào thực thể hiện có và giữ ảnh cũ nếu không có ảnh mới.
    public Book updateBookFromRequest(Book book, BookRequest request, MultipartFile imageFile) throws Exception {
        // Tải các thực thể liên quan.
        Author author = loadAuthor(request.getAuthorId());
        Category category = loadCategory(request.getCategoryId());
        Discount discount = loadDiscount(request.getDiscountId());

        // Cập nhật ảnh nếu có tệp mới.
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileUploadService.uploadFile(imageFile, "books");
            book.setCoverImage(imageUrl);
        }

        // Cập nhật thông tin sách.
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

    // Tải tác giả được chọn hoặc báo lỗi khi không tồn tại.
    private Author loadAuthor(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));
    }

    // Tải danh mục được chọn hoặc báo lỗi khi không tồn tại.
    private Category loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    }

    // Tải chương trình giảm giá nếu yêu cầu có truyền mã giảm giá.
    private Discount loadDiscount(Long discountId) {
        if (discountId == null) {
            return null;
        }
        return discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình giảm giá"));
    }

    // Upload ảnh bìa hợp lệ và trả URL, hoặc null khi không có file.
    private String uploadImageIfProvided(MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }
        return fileUploadService.uploadFile(imageFile, "books");
    }
}

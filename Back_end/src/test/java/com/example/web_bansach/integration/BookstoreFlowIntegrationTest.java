package com.example.web_bansach.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.dto.request.AddToCartRequest;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.CartCommandService;
import com.example.web_bansach.module.category.entity.Category;
import com.example.web_bansach.module.category.repository.CategoryRepository;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.order.service.OrderUserService;
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.repository.ReviewRepository;
import com.example.web_bansach.module.review.service.ReviewService;
import com.example.web_bansach.module.user.entity.Roles;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.RolesRepository;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.user.service.AuthService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookstoreFlowIntegrationTest {

    @Autowired private AuthService authService;
    @Autowired private RolesRepository rolesRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartCommandService cartCommandService;
    @Autowired private OrderUserService orderUserService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ReviewService reviewService;
    @Autowired private ReviewRepository reviewRepository;

    @Test
    void register_shouldCreateUserWithRoleUser() {
        ensureRoleUserExists();

        UserRequest request = new UserRequest();
        request.setUsername("student01");
        request.setEmail("student01@test.com");
        request.setPassword("secret123");
        request.setFullName("Student One");

        authService.taoTaiKhoanMoi(request);

        Users user = userRepository.findByEmail("student01@test.com");
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).extracting(Roles::getName).contains("ROLE_USER");
    }

    @Test
    void cartOrderAndReviewFlow_shouldWorkTogether() {
        Users user = saveUser("buyer@test.com");
        Book book = saveBookWithInventory(5);

        AddToCartRequest addToCartRequest = new AddToCartRequest();
        addToCartRequest.setBookId(book.getId());
        addToCartRequest.setQuantity(2);
        cartCommandService.addToCart(user.getEmail(), addToCartRequest);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setReceiverName("Nguyen Van A");
        orderRequest.setReceiverPhone("0901234567");
        orderRequest.setShippingAddress("Ha Noi");
        orderRequest.setShippingFee(BigDecimal.ZERO);

        OrderResponse orderResponse = orderUserService.createOrder(user.getEmail(), orderRequest);

        Inventory inventory = inventoryRepository.findByBookId(book.getId()).orElseThrow();
        assertThat(inventory.getQuantity()).isEqualTo(3);

        Long cartId = cartRepository.findByUserId(user.getId()).orElseThrow().getId();
        assertThat(cartItemRepository.findByCartIdWithBook(cartId)).isEmpty();
        assertThat(orderResponse.getTotalAmount()).isEqualByComparingTo(new BigDecimal("200000"));

        Order order = orderRepository.findById(orderResponse.getId()).orElseThrow();
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        CreateReviewRequest reviewRequest = new CreateReviewRequest();
        reviewRequest.setBookId(book.getId());
        reviewRequest.setRating(5);
        reviewRequest.setComment("Sach tot");

        ReviewResponse review = reviewService.createReview(user.getEmail(), reviewRequest);

        assertThat(review.getRating()).isEqualTo(5);
        assertThat(reviewRepository.findByUserIdAndBookId(user.getId(), book.getId())).isPresent();
    }

    private Users saveUser(String email) {
        Roles roleUser = ensureRoleUserExists();

        Users user = new Users();
        user.setUsername(email.substring(0, email.indexOf("@")));
        user.setEmail(email);
        user.setPassword("encoded");
        user.setFullName("Buyer");
        user.setIsActive(true);
        user.setRoles(Set.of(roleUser));
        return userRepository.save(user);
    }

    private Roles ensureRoleUserExists() {
        Roles existing = rolesRepository.findByName("ROLE_USER");
        if (existing != null) {
            return existing;
        }

        Roles role = new Roles();
        role.setName("ROLE_USER");
        return rolesRepository.save(role);
    }

    private Book saveBookWithInventory(int quantity) {
        Author author = new Author();
        author.setAuthorName("Author A");
        author = authorRepository.save(author);

        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Programming books");
        category.setIsActive(true);
        category = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Spring Boot");
        book.setIsbn("ISBN-" + System.nanoTime());
        book.setPublisher("NXB");
        book.setPublicationYear(2026);
        book.setPrice(new BigDecimal("100000"));
        book.setAuthor(author);
        book.setCategory(category);
        book = bookRepository.save(book);

        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);

        return book;
    }
}

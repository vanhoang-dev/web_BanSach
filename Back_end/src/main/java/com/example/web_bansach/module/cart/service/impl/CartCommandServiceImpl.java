package com.example.web_bansach.module.cart.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.dto.request.AddToCartRequest;
import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.mapper.CartItemMapper;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.CartCommandService;
import com.example.web_bansach.module.cart.service.CartValidationService;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.pricing.service.PricingService;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@Service
public class CartCommandServiceImpl implements CartCommandService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final PricingService pricingService;
    private final CartItemMapper cartItemMapper;
    private final CartValidationService cartValidationService;

    public CartCommandServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            InventoryRepository inventoryRepository,
            PricingService pricingService,
            CartItemMapper cartItemMapper,
            CartValidationService cartValidationService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.inventoryRepository = inventoryRepository;
        this.pricingService = pricingService;
        this.cartItemMapper = cartItemMapper;
        this.cartValidationService = cartValidationService;
    }

    @Transactional
    public Cart getOrCreateCart(String username) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse addToCart(String username, AddToCartRequest request) {
        cartValidationService.validateQuantity(request.getQuantity());

        Cart cart = getOrCreateCart(username);
        Book book = bookRepository.findByIdWithJoin(request.getBookId());
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        if (book.getDeletedAt() != null) {
            throw new BusinessException("Sách này không còn khả dụng");
        }
        Inventory inventory = inventoryRepository.findByBookId(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tồn kho"));
        if (inventory.getQuantity() == null || inventory.getQuantity() <= 0) {
            throw new BusinessException("Sách này hiện đã hết hàng");
        }
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new BusinessException(
                    "Số lượng sách không đủ. Hiện còn: " + inventory.getQuantity() + " cuốn");
        }

        CartItem cartItem = cartItemRepository
                .findByCartIdAndBookId(cart.getId(), book.getId())
                .orElse(null);
        if (cartItem != null) {
            int totalQty = cartItem.getQuantity() + request.getQuantity();
            if (totalQty > inventory.getQuantity()) {
                throw new BusinessException(
                        "Tổng số lượng vượt quá tồn kho. Hiện còn: " + inventory.getQuantity() + " cuốn");
            }
            cartItem.setQuantity(totalQty);
        } else {
            BigDecimal price = pricingService.calculateBookPrice(book);
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(price);
        }
        cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return getCart(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse updateCartItem(String username, Long itemId, Integer quantity) {
        cartValidationService.validateQuantity(quantity);

        Cart cart = getOrCreateCart(username);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Sản phẩm không thuộc giỏ hàng của bạn");
        }
        Inventory inventory = inventoryRepository.findByBookId(cartItem.getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tồn kho"));
        if (inventory.getQuantity() == null || quantity > inventory.getQuantity()) {
            throw new BusinessException(
                    "Số lượng sách không đủ. Hiện còn: " + inventory.getQuantity() + " cuốn");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return getCart(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse removeCartItem(String username, Long itemId) {
        Cart cart = getOrCreateCart(username);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Sản phẩm không thuộc giỏ hàng của bạn");
        }
        cartItemRepository.delete(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return getCart(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private CartResponse getCart(String username) {
        Cart cart = getOrCreateCart(username);
        List<CartItem> items = cartItemRepository.findByCartIdWithBook(cart.getId());
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(items.size());
        List<CartItemResponse> itemResponses = items.stream()
                .map(cartItemMapper::mapToResponse)
                .collect(Collectors.toList());
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);
        return response;
    }
}

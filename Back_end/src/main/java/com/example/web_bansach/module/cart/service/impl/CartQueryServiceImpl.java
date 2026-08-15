package com.example.web_bansach.module.cart.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.CartQueryService;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@Service
// Đọc giỏ hàng và tính giá hiển thị mà không làm thay đổi database.
public class CartQueryServiceImpl implements CartQueryService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    // Khởi tạo service với repository người dùng, giỏ, dòng hàng và mapper.
    public CartQueryServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    // Trả giỏ cùng giá sau giảm, thành tiền và tổng số lượng.
    public CartResponse getCart(String username) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            CartResponse empty = new CartResponse();
            empty.setTotalItems(0);
            empty.setCartId(null);
            empty.setItems(List.of());
            empty.setTotalAmount(BigDecimal.ZERO);
            return empty;
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart == null) {
            return emptyCart();
        }

        List<CartItem> items = cartItemRepository.findByCartIdWithBook(cart.getId());
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(items.size());
        List<CartItemResponse> itemResponses = items.stream()
                .map(item -> {
                    CartItemResponse resp = new CartItemResponse();
                    resp.setId(item.getId());
                    resp.setBookId(item.getBook().getId());
                    resp.setBookTitle(item.getBook().getTitle());
                    resp.setBookCoverImage(item.getBook().getCoverImage());
                    resp.setBookPrice(item.getBook().getPrice());
                    BigDecimal priceAfterDiscount = calculatePrice(item.getBook());
                    Integer discountPercent = (item.getBook().getDiscount() != null
                            && item.getBook().getDiscount().getIsActive())
                                    ? item.getBook().getDiscount().getDiscountPercent()
                                    : null;
                    resp.setDiscountPercent(discountPercent);
                    resp.setPriceAfterDiscount(priceAfterDiscount);
                    resp.setQuantity(item.getQuantity());
                    BigDecimal subtotal = priceAfterDiscount.multiply(new BigDecimal(item.getQuantity()));
                    resp.setSubtotal(subtotal);
                    return resp;
                })
                .collect(Collectors.toList());
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);
        return response;
    }

    // Tính đơn giá sách sau khi áp dụng chương trình giảm giá còn hiệu lực.
    private BigDecimal calculatePrice(com.example.web_bansach.module.book.entity.Book book) {
        BigDecimal price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
        if (book.getDiscount() != null
                && Boolean.TRUE.equals(book.getDiscount().getIsActive())
                && book.getDiscount().getDiscountPercent() != null) {
            BigDecimal discountPercent = new BigDecimal(book.getDiscount().getDiscountPercent());
            BigDecimal discountAmount = price.multiply(discountPercent).divide(new BigDecimal(100));
            price = price.subtract(discountAmount);
        }
        return price;
    }

    // Tạo response giỏ rỗng để GET không phải phát sinh bản ghi mới.
    private CartResponse emptyCart() {
        CartResponse empty = new CartResponse();
        empty.setTotalItems(0);
        empty.setCartId(null);
        empty.setItems(List.of());
        empty.setTotalAmount(BigDecimal.ZERO);
        return empty;
    }
}

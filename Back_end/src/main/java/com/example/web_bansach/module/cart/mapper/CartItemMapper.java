package com.example.web_bansach.module.cart.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.pricing.service.PricingService;

/**
 * Mapper xử lý mapping Cart entity sang CartResponse
 */
@Component
public class CartItemMapper {

    private final PricingService pricingService;

    public CartItemMapper(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Map CartItem sang CartItemResponse
     * Sử dụng PricingService để tính giá
     */
    public CartItemResponse mapToResponse(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setBookTitle(item.getBook().getTitle());
        response.setBookCoverImage(item.getBook().getCoverImage());
        response.setBookPrice(item.getBook().getPrice());

        // Calculate price with discount
        BigDecimal priceAfterDiscount = pricingService.calculateBookPrice(item.getBook());
        response.setPriceAfterDiscount(priceAfterDiscount);

        // Get discount percent if available
        Integer discountPercent = pricingService.getDiscountPercent(item.getBook());
        response.setDiscountPercent(discountPercent > 0 ? discountPercent : null);

        // Set quantity
        response.setQuantity(item.getQuantity());

        // Calculate subtotal
        BigDecimal subtotal = priceAfterDiscount.multiply(new BigDecimal(item.getQuantity()));
        response.setSubtotal(subtotal);

        return response;
    }
}

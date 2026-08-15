package com.example.web_bansach.module.cart.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.pricing.service.PricingService;

/**
 * Chuyển thực thể giỏ hàng thành dữ liệu phản hồi.
 */
@Component
public class CartItemMapper {

    private final PricingService pricingService;

    public CartItemMapper(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Chuyển một dòng sản phẩm trong giỏ thành dữ liệu phản hồi.
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

        // Tính giá sản phẩm sau khi áp dụng giảm giá.
        BigDecimal priceAfterDiscount = pricingService.calculateBookPrice(item.getBook());
        response.setPriceAfterDiscount(priceAfterDiscount);

        // Lấy phần trăm giảm giá nếu đang có hiệu lực.
        Integer discountPercent = pricingService.getDiscountPercent(item.getBook());
        response.setDiscountPercent(discountPercent > 0 ? discountPercent : null);

        // Set quantity
        response.setQuantity(item.getQuantity());

        // Tính thành tiền của sản phẩm.
        BigDecimal subtotal = priceAfterDiscount.multiply(new BigDecimal(item.getQuantity()));
        response.setSubtotal(subtotal);

        return response;
    }
}

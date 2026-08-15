package com.example.web_bansach.module.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.web_bansach.module.order.entity.OrderStatus;

import lombok.Data;

@Data
// Trả đầy đủ đơn hàng, giao nhận, voucher, tổng tiền và các sản phẩm.
public class OrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String shippingMethod;
    private BigDecimal shippingFee;
    private String voucherCode; // Mã voucher áp dụng
    private BigDecimal voucherDiscount; // Số tiền giảm từ voucher
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}

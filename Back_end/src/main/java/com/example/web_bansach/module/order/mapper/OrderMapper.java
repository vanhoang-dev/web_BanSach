package com.example.web_bansach.module.order.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.order.dto.response.OrderItemResponse;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.repository.OrderItemRepository;

/**
 * Mapper xử lý mapping Order entity sang OrderResponse
 */
@Component
public class OrderMapper {

    private final OrderItemRepository orderItemRepository;

    public OrderMapper(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Map Order entity sang OrderResponse
     * Dùng JOIN FETCH để tránh N+1 problem
     */
    public OrderResponse mapToResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setVoucherCode(order.getVoucherCode());
        response.setVoucherDiscount(order.getVoucherDiscount());
        response.setOrderDate(order.getOrderDate());

        // Load order items với JOIN FETCH (tránh N+1)
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(this::mapOrderItemToResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }

    /**
     * Map OrderItem sang OrderItemResponse
     */
    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemResponse response = new OrderItemResponse();
        response.setBookId(item.getBook().getId());
        response.setBookTitle(item.getBook().getTitle());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());

        return response;
    }
}

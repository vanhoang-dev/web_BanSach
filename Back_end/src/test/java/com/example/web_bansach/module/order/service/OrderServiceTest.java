package com.example.web_bansach.module.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private RealtimeNotificationService realtimeNotificationService;

    @Test
    void updateOrderStatus_shouldAllowSequentialBusinessTransition() {
        OrderService orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                inventoryRepository,
                orderMapper,
                realtimeNotificationService);
        Order order = order(OrderStatus.PENDING);
        OrderResponse response = new OrderResponse();
        response.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.mapToResponse(order)).thenReturn(response);

        OrderResponse actual = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void updateOrderStatus_shouldRejectSkippingFromPendingToCompleted() {
        OrderService orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                inventoryRepository,
                orderMapper,
                realtimeNotificationService);
        Order order = order(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.COMPLETED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("khong hop le");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_shouldRejectMovingShippingBackToConfirmed() {
        OrderService orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                inventoryRepository,
                orderMapper,
                realtimeNotificationService);
        Order order = order(OrderStatus.SHIPPING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("khong hop le");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);
        verify(orderRepository, never()).save(any(Order.class));
    }

    private Order order(OrderStatus status) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        return order;
    }
}

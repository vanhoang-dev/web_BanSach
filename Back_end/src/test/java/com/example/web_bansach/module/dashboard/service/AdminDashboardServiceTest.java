package com.example.web_bansach.module.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.dashboard.dto.AdminDashboardResponse;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.payment.repository.PaymentRepository;
import com.example.web_bansach.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderMapper orderMapper;

    @Test
    void getDashboard_shouldUseSuccessfulPaymentsForRevenue() {
        AdminDashboardService service = new AdminDashboardService(
                orderRepository,
                orderItemRepository,
                paymentRepository,
                bookRepository,
                userRepository,
                orderMapper);

        Order order = new Order();
        order.setId(1L);
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1L);

        when(orderRepository.count()).thenReturn(10L);
        when(paymentRepository.countByStatus("SUCCESS")).thenReturn(7L);
        when(paymentRepository.sumAmountByStatus("SUCCESS")).thenReturn(new BigDecimal("500000"));
        when(bookRepository.countByDeletedAtIsNull()).thenReturn(20L);
        when(userRepository.countByDeletedAtIsNull()).thenReturn(30L);
        when(orderItemRepository.sumSoldQuantityForSuccessfulPayments()).thenReturn(12L);
        when(orderRepository.findTop5ByOrderByOrderDateDesc()).thenReturn(List.of(order));
        when(orderMapper.mapToResponse(order)).thenReturn(orderResponse);

        AdminDashboardResponse response = service.getDashboard();

        assertThat(response.getTotalOrders()).isEqualTo(10L);
        assertThat(response.getTotalPaidPayments()).isEqualTo(7L);
        assertThat(response.getTotalRevenue()).isEqualByComparingTo("500000");
        assertThat(response.getTotalBooksSold()).isEqualTo(12L);
        assertThat(response.getRecentOrders()).hasSize(1);
    }
}

package com.example.web_bansach.module.dashboard.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.common.cache.CacheNames;
import com.example.web_bansach.module.dashboard.dto.AdminDashboardResponse;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.payment.repository.PaymentRepository;
import com.example.web_bansach.module.user.repository.UserRepository;

@Service
// Tổng hợp thống kê người dùng, sách, đơn hàng, doanh thu và tồn kho.
public class AdminDashboardService {
    private static final String PAYMENT_SUCCESS = "SUCCESS";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public AdminDashboardService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.DASHBOARD, key = "'summary'")
    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setTotalOrders(orderRepository.count());
        response.setTotalPaidPayments(paymentRepository.countByStatus(PAYMENT_SUCCESS));
        response.setTotalRevenue(paymentRepository.sumAmountByStatus(PAYMENT_SUCCESS));
        response.setTotalBooks(bookRepository.countByDeletedAtIsNull());
        response.setTotalUsers(userRepository.countByDeletedAtIsNull());
        response.setTotalBooksSold(orderItemRepository.sumSoldQuantityForSuccessfulPayments());
        response.setRecentOrders(orderRepository.findTop5ByOrderByOrderDateDesc()
                .stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList()));
        return response;
    }
}

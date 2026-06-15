package com.example.web_bansach.module.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.web_bansach.module.order.dto.response.OrderResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalOrders;
    private long totalPaidPayments;
    private BigDecimal totalRevenue;
    private long totalBooks;
    private long totalUsers;
    private long totalBooksSold;
    private List<OrderResponse> recentOrders;
}

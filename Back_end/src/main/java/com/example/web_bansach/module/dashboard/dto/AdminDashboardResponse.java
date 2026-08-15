package com.example.web_bansach.module.dashboard.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.example.web_bansach.module.order.dto.response.OrderResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Đóng gói các chỉ số tổng quan và danh sách gần đây cho dashboard admin.
public class AdminDashboardResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private long totalOrders;
    private long totalPaidPayments;
    private BigDecimal totalRevenue;
    private long totalBooks;
    private long totalUsers;
    private long totalBooksSold;
    private List<OrderResponse> recentOrders;
}

package com.example.web_bansach.module.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;

/**
 * Dịch vụ xử lý nghiệp vụ quản trị đơn hàng.
 */
@Service
// Xử lý truy vấn và thay đổi trạng thái đơn hàng từ phía quản trị.
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;
    private final RealtimeNotificationService realtimeNotificationService;

    // Khởi tạo service với repository đơn, tồn kho và mapper.
    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository,
            OrderMapper orderMapper,
            RealtimeNotificationService realtimeNotificationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderMapper = orderMapper;
        this.realtimeNotificationService = realtimeNotificationService;
    }

    @Transactional(readOnly = true)
    // Trả toàn bộ đơn hàng theo trang cho quản trị viên.
    public Page<OrderResponse> getAllOrders(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BusinessException("Tham số phân trang không hợp lệ");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    // Trả chi tiết một đơn hàng bất kỳ theo ID.
    public OrderResponse getOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        return orderMapper.mapToResponse(order);
    }

    @Transactional
    // Chuyển trạng thái đơn và cập nhật tồn kho tại mốc nghiệp vụ phù hợp.
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (status == null) {
            throw new BusinessException("Trạng thái không hợp lệ");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng đã bị hủy, không thể cập nhật");
        }

        if (order.getStatus() == OrderStatus.COMPLETED && status != OrderStatus.COMPLETED) {
            throw new BusinessException("Đơn hàng đã hoàn thành, không thể thay đổi trạng thái");
        }

        if (!isAllowedStatusTransition(order.getStatus(), status)) {
            throw new BusinessException("Chuyen trang thai don hang khong hop le");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        realtimeNotificationService.publishOrderEvent(
            "ORDER_STATUS_UPDATED",
            updatedOrder.getId(),
            updatedOrder.getUser() != null ? updatedOrder.getUser().getUsername() : null,
            "Trạng thái đơn hàng đã thay đổi",
            status.name(),
            java.util.Map.of(
                "orderId", updatedOrder.getId(),
                "status", status.name()));

        return orderMapper.mapToResponse(updatedOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    // Hủy đơn từ phía quản trị viên và hoàn tồn kho khi cần.
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (order.getStatus() == OrderStatus.SHIPPING) {
            throw new BusinessException("Không thể hủy đơn hàng đang giao");
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy đơn hàng đã hoàn thành");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng đã bị hủy trước đó");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
            for (OrderItem item : orderItems) {
                Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId()).orElse(null);
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        realtimeNotificationService.publishOrderEvent(
            "ORDER_CANCELLED",
            order.getId(),
            order.getUser() != null ? order.getUser().getUsername() : null,
            "Đơn hàng đã bị hủy",
            OrderStatus.CANCELLED.name(),
            java.util.Map.of(
                "orderId", order.getId(),
                "status", OrderStatus.CANCELLED.name()));
    }

    private boolean isAllowedStatusTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        if (currentStatus == null || nextStatus == null || currentStatus == nextStatus) {
            return true;
        }

        return switch (currentStatus) {
            case PENDING -> nextStatus == OrderStatus.CONFIRMED || nextStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> nextStatus == OrderStatus.SHIPPING || nextStatus == OrderStatus.CANCELLED;
            case SHIPPING -> nextStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}

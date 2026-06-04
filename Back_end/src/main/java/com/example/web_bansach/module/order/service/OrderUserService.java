package com.example.web_bansach.module.order.service;

import java.math.BigDecimal;
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
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.mapper.OrderMapper;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.voucher.service.VoucherService;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;

/**
 * Service xử lý tạo order từ phía user
 * Sử dụng constructor injection thay vì field injection
 */
@Service
public class OrderUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final VoucherService voucherService;
    private final OrderMapper orderMapper;
    private final RealtimeNotificationService realtimeNotificationService;

    public OrderUserService(UserRepository userRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            InventoryRepository inventoryRepository,
            VoucherService voucherService,
            OrderMapper orderMapper,
            RealtimeNotificationService realtimeNotificationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.voucherService = voucherService;
        this.orderMapper = orderMapper;
        this.realtimeNotificationService = realtimeNotificationService;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        var cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Giỏ hàng trống, không thể tạo đơn"));
        List<CartItem> cartItems = cartItemRepository.findByCartIdWithBook(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("Giỏ hàng trống, không thể tạo đơn");
        }

        // Kiểm tra tồn kho cho tất cả items trước khi tạo order
        for (CartItem item : cartItems) {
            Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Không tìm thấy bản ghi tồn kho cho sách: " + item.getBook().getTitle()));

            if (inventory.getQuantity() == null || inventory.getQuantity() <= 0) {
                throw new BusinessException(
                        "Sách '" + item.getBook().getTitle() + "' đã hết hàng");
            }

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "Số lượng sách '" + item.getBook().getTitle() + "' không đủ. "
                                + "Yêu cầu: " + item.getQuantity() + ", Hiện còn: " + inventory.getQuantity());
            }
        }

        BigDecimal itemsTotal = cartItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = request.getShippingFee() == null ? BigDecimal.ZERO : request.getShippingFee();
        BigDecimal totalAmount = itemsTotal.add(shippingFee);
        BigDecimal voucherDiscount = BigDecimal.ZERO;

        // Xử lý voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            try {
                var voucherResponse = voucherService.getVoucherByCode(request.getVoucherCode());

                // Tính tiền giảm: (discountPercent / 100) * itemsTotal, nhưng không vượt quá
                // maxDiscount
                BigDecimal discountAmount = itemsTotal.multiply(new BigDecimal(voucherResponse.getDiscountPercent()))
                        .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);

                if (discountAmount.compareTo(voucherResponse.getMaxDiscount()) > 0) {
                    discountAmount = voucherResponse.getMaxDiscount();
                }

                voucherDiscount = discountAmount;
                totalAmount = totalAmount.subtract(voucherDiscount);

                // Đảm bảo tổng không âm
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    totalAmount = BigDecimal.ZERO;
                }
            } catch (ResourceNotFoundException | BusinessException e) {
                // Nếu voucher không hợp lệ, vẫn tạo order mà không giảm giá
                // Có thể log warning ở đây nếu cần
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setReceiverName(request.getReceiverName().trim());
        order.setReceiverPhone(request.getReceiverPhone().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(shippingFee);
        order.setVoucherCode(request.getVoucherCode() != null ? request.getVoucherCode().toUpperCase() : null);
        order.setVoucherDiscount(voucherDiscount);
        order.setTotalAmount(totalAmount);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Tạo OrderItems từ CartItems và giảm Inventory
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);

            // Giảm Inventory quantity
            Inventory inventory = inventoryRepository.findByBookId(cartItem.getBook().getId()).get();
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);
        }

        // Sử dụng voucher nếu đã áp dụng thành công
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()
                && voucherDiscount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                voucherService.useVoucher(request.getVoucherCode());
            } catch (Exception e) {
                // Log warning nếu cần, nhưng không throw exception vì order đã tạo
            }
        }

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        realtimeNotificationService.publishOrderEvent(
            "ORDER_CREATED",
            savedOrder.getId(),
            user.getUsername(),
            "Đơn hàng mới đã được tạo",
            "PENDING",
            java.util.Map.of(
                "orderId", savedOrder.getId(),
                "username", user.getUsername(),
                "totalAmount", savedOrder.getTotalAmount(),
                "status", savedOrder.getStatus().name()));

        return orderMapper.mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(String username, int page, int size) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(orderMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderDetail(String username, Long orderId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }

        return orderMapper.mapToResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelMyOrder(String username, Long orderId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }

        if (order.getStatus() == OrderStatus.SHIPPING) {
            throw new BusinessException("Không thể hủy đơn hàng đang giao");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy đơn hàng đã hoàn thành");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng đã bị hủy trước đó");
        }

        // Hoàn lại inventory
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId()).get();
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        realtimeNotificationService.publishOrderEvent(
            "ORDER_CANCELLED",
            order.getId(),
            user.getUsername(),
            "Đơn hàng đã bị hủy",
            OrderStatus.CANCELLED.name(),
            java.util.Map.of(
                "orderId", order.getId(),
                "username", user.getUsername(),
                "status", OrderStatus.CANCELLED.name()));
    }
}

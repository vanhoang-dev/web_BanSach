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
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.request.BuyNowOrderRequest;
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
    private final BookRepository bookRepository;
    private final VoucherService voucherService;
    private final OrderMapper orderMapper;
    private final RealtimeNotificationService realtimeNotificationService;
    private final OrderValidationService orderValidationService;

    public OrderUserService(UserRepository userRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            InventoryRepository inventoryRepository,
            BookRepository bookRepository,
            VoucherService voucherService,
            OrderMapper orderMapper,
            RealtimeNotificationService realtimeNotificationService,
            OrderValidationService orderValidationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookRepository = bookRepository;
        this.voucherService = voucherService;
        this.orderMapper = orderMapper;
        this.realtimeNotificationService = realtimeNotificationService;
        this.orderValidationService = orderValidationService;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        orderValidationService.validateCreateOrder(request);

        Users user = userRepository.findByEmail(username);
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
            var voucherResponse = voucherService.getMyVoucherByCode(username, request.getVoucherCode());
            if (voucherResponse == null) {
                throw new BusinessException("Voucher không thuộc tài khoản hoặc không hợp lệ");
            }

            BigDecimal discountAmount = itemsTotal.multiply(new BigDecimal(voucherResponse.getDiscountPercent()))
                    .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);

            if (discountAmount.compareTo(voucherResponse.getMaxDiscount()) > 0) {
                discountAmount = voucherResponse.getMaxDiscount();
            }

            voucherDiscount = discountAmount;
            totalAmount = totalAmount.subtract(voucherDiscount);

            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
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
            Inventory inventory = inventoryRepository.findByBookIdForUpdate(cartItem.getBook().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Không tìm thấy bản ghi tồn kho cho sách: " + cartItem.getBook().getTitle()));
            if (inventory.getQuantity() == null || inventory.getQuantity() < cartItem.getQuantity()) {
                throw new BusinessException(
                        "Số lượng sách '" + cartItem.getBook().getTitle() + "' không đủ. "
                                + "Yêu cầu: " + cartItem.getQuantity() + ", Hiện còn: " + inventory.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);

            // Giảm Inventory quantity
        }

        // Sử dụng voucher nếu đã áp dụng thành công
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()
                && voucherDiscount.compareTo(BigDecimal.ZERO) > 0) {
            voucherService.useOwnedVoucher(username, request.getVoucherCode());
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

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse buyNow(String username, BuyNowOrderRequest request) {
        orderValidationService.validateCreateOrder(request);
        validateBuyNowRequest(request);

        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("KhÃ´ng tÃ¬m tháº¥y sÃ¡ch"));

        if (book.getDeletedAt() != null) {
            throw new BusinessException("SÃ¡ch khÃ´ng kháº£ dá»¥ng");
        }

        Inventory inventory = inventoryRepository.findByBookIdForUpdate(book.getId())
                .orElseThrow(() -> new BusinessException("KhÃ´ng tÃ¬m tháº¥y báº£n ghi tá»“n kho cho sÃ¡ch: " + book.getTitle()));

        if (inventory.getQuantity() == null || inventory.getQuantity() < request.getQuantity()) {
            throw new BusinessException("Sá»‘ lÆ°á»£ng sÃ¡ch '" + book.getTitle() + "' khÃ´ng Ä‘á»§");
        }

        BigDecimal itemsTotal = book.getPrice().multiply(new BigDecimal(request.getQuantity()));
        BigDecimal shippingFee = request.getShippingFee() == null ? BigDecimal.ZERO : request.getShippingFee();
        BigDecimal totalAmount = itemsTotal.add(shippingFee);
        BigDecimal voucherDiscount = calculateVoucherDiscount(username, request.getVoucherCode(), itemsTotal);
        totalAmount = totalAmount.subtract(voucherDiscount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
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

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setBook(book);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(book.getPrice());
        orderItemRepository.save(orderItem);

        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()
                && voucherDiscount.compareTo(BigDecimal.ZERO) > 0) {
            voucherService.useOwnedVoucher(username, request.getVoucherCode());
        }

        realtimeNotificationService.publishOrderEvent(
            "ORDER_CREATED",
            savedOrder.getId(),
            user.getUsername(),
            "ÄÆ¡n hÃ ng má»›i Ä‘Ã£ Ä‘Æ°á»£c táº¡o",
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
        validatePageRequest(page, size);

        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(orderMapper::mapToResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderDetail(String username, Long orderId) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }

        return orderMapper.mapToResponse(order);
    }

    private void validatePageRequest(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BusinessException("Tham số phân trang không hợp lệ");
        }
    }

    private void validateBuyNowRequest(BuyNowOrderRequest request) {
        if (request.getBookId() == null || request.getBookId() <= 0) {
            throw new BusinessException("ID sach khong hop le");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessException("So luong phai lon hon 0");
        }
    }

    private BigDecimal calculateVoucherDiscount(String username, String voucherCode, BigDecimal itemsTotal) {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        var voucherResponse = voucherService.getMyVoucherByCode(username, voucherCode);
        if (voucherResponse == null) {
            throw new BusinessException("Voucher không thuộc tài khoản hoặc không hợp lệ");
        }
        BigDecimal discountAmount = itemsTotal.multiply(new BigDecimal(voucherResponse.getDiscountPercent()))
                .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);

        if (discountAmount.compareTo(voucherResponse.getMaxDiscount()) > 0) {
            return voucherResponse.getMaxDiscount();
        }
        return discountAmount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelMyOrder(String username, Long orderId) {
        Users user = userRepository.findByEmail(username);
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
        if (order.getStatus() != OrderStatus.PENDING) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
            for (OrderItem item : orderItems) {
                Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId()).get();
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                inventoryRepository.save(inventory);
            }
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

package com.example.web_bansach.module.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.infrastructure.payment.PaymentGateway;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.entity.Payment;
import com.example.web_bansach.module.payment.repository.PaymentRepository;
import com.example.web_bansach.module.payment.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentGateway paymentGateway;
    private final RealtimeNotificationService realtimeNotificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository,
            PaymentGateway paymentGateway,
            RealtimeNotificationService realtimeNotificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.paymentGateway = paymentGateway;
        this.realtimeNotificationService = realtimeNotificationService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResponse initiatePayment(String userEmail, PaymentRequest request) throws Exception {
        if (request == null) {
            throw new BusinessException("Thong tin thanh toan khong hop le");
        }

        validatePaymentRequest(request);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don hang"));
        validateOrderCanBePaidByUser(order, userEmail, request.getAmount());

        String paymentUrl = paymentGateway.initiatePayment(
                request.getOrderId(),
                request.getAmount(),
                request.getReturnUrl(),
                request.getDescription());

        String transactionId = buildPaymentCode(order.getId());

        Payment payment = paymentRepository.findByOrder_Id(order.getId()).orElseGet(Payment::new);
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod("SEPAY");
        payment.setStatus("PENDING");
        payment.setPaymentUrl(paymentUrl);
        payment.setTransactionId(transactionId);

        Payment savedPayment = paymentRepository.save(payment);

        realtimeNotificationService.publishPaymentEvent(
                "PAYMENT_INITIATED",
                savedPayment.getId(),
                order.getId(),
                "Thanh toan da duoc khoi tao",
                "PENDING",
                java.util.Map.of(
                        "paymentId", savedPayment.getId(),
                        "orderId", order.getId(),
                        "transactionId", transactionId,
                        "amount", request.getAmount(),
                        "status", "PENDING"));

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(savedPayment.getId());
        response.setPaymentUrl(paymentUrl);
        response.setTransactionId(transactionId);
        response.setAmount(request.getAmount());
        response.setStatus("PENDING");
        response.setMessage("Chuyen huong toi trang thanh toan...");

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean verifyPaymentCallback(String transactionId, BigDecimal amount, String signature) {
        if (transactionId == null || amount == null || signature == null) {
            return false;
        }

        Payment payment = findPaymentByTransactionId(transactionId).orElse(null);
        if (payment == null) {
            return false;
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(amount) != 0) {
            return false;
        }

        return paymentGateway.verifyPayment(payment.getTransactionId(), amount, signature);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentStatus(String userEmail, boolean admin, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thong tin thanh toan"));

        validatePaymentVisibleToUser(payment, userEmail, admin);
        return toPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentStatusByOrderId(String userEmail, boolean admin, Long orderId) {
        Payment payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thong tin thanh toan"));

        validatePaymentVisibleToUser(payment, userEmail, admin);
        return toPaymentResponse(payment);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePaymentStatus(String transactionId, String status, String signature) {
        Payment payment = findPaymentByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thong tin thanh toan"));

        payment.setStatus(status);
        payment.setCallbackSignature(signature);
        payment.setCallbackReceivedAt(LocalDateTime.now());
        payment.setCallbackVerified(true);

        if ("SUCCESS".equals(status)) {
            payment.setPaidAt(LocalDateTime.now());
            Order order = payment.getOrder();
            if (order != null && order.getStatus() == OrderStatus.PENDING) {
                deductInventoryForPaidOrder(order);
                order.setStatus(OrderStatus.CONFIRMED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            }
        }

        paymentRepository.save(payment);

        realtimeNotificationService.publishPaymentEvent(
                "PAYMENT_STATUS_UPDATED",
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                "Trang thai thanh toan da thay doi",
                status,
                java.util.Map.of(
                        "paymentId", payment.getId(),
                        "transactionId", transactionId,
                        "status", status,
                        "signatureVerified", true));
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentUrl(payment.getPaymentUrl());
        response.setStatus(payment.getStatus() != null ? payment.getStatus() : "PENDING");

        return response;
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getOrderId() == null || request.getOrderId() <= 0) {
            throw new BusinessException("ID don hang khong hop le");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("So tien thanh toan phai lon hon 0");
        }

        if (request.getReturnUrl() == null || request.getReturnUrl().trim().isEmpty()) {
            throw new BusinessException("URL tra ve khong hop le");
        }
    }

    private void validateOrderCanBePaidByUser(Order order, String userEmail, BigDecimal amount) {
        if (order.getUser() == null || order.getUser().getEmail() == null
                || !order.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Ban khong co quyen thanh toan don hang nay");
        }

        if (order.getStatus() == null || order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Chi co the thanh toan don hang dang cho xu ly");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(amount) != 0) {
            throw new BusinessException("So tien thanh toan khong khop voi tong tien don hang");
        }
    }

    private Optional<Payment> findPaymentByTransactionId(String transactionId) {
        String normalized = normalizePaymentCode(transactionId);
        Optional<Payment> payment = paymentRepository.findByTransactionId(normalized);
        if (payment.isPresent()) {
            return payment;
        }

        String legacy = toLegacyPaymentCode(normalized);
        if (!legacy.equals(normalized)) {
            return paymentRepository.findByTransactionId(legacy);
        }

        return Optional.empty();
    }

    private String buildPaymentCode(Long orderId) {
        return "SEP" + orderId;
    }

    private String normalizePaymentCode(String transactionId) {
        return transactionId == null ? "" : transactionId.trim().toUpperCase().replace("-", "");
    }

    private String toLegacyPaymentCode(String normalizedCode) {
        if (normalizedCode != null && normalizedCode.startsWith("SEP") && normalizedCode.length() > 3) {
            return "SEP-" + normalizedCode.substring(3);
        }
        return normalizedCode;
    }

    private void deductInventoryForPaidOrder(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryRepository.findByBookIdForUpdate(item.getBook().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Khong tim thay ban ghi ton kho cho sach: " + item.getBook().getTitle()));

            if (inventory.getQuantity() == null || inventory.getQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "So luong sach '" + item.getBook().getTitle() + "' khong du de xac nhan don hang");
            }

            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    private void validatePaymentVisibleToUser(Payment payment, String userEmail, boolean admin) {
        if (admin) {
            return;
        }

        Order order = payment.getOrder();
        if (order == null || order.getUser() == null || order.getUser().getEmail() == null
                || !order.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Ban khong co quyen xem thong tin thanh toan nay");
        }
    }
}

package com.example.web_bansach.module.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.payment.PaymentGateway;
import com.example.web_bansach.infrastructure.realtime.RealtimeNotificationService;
import com.example.web_bansach.module.book.entity.Book;
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
import com.example.web_bansach.module.payment.service.impl.PaymentServiceImpl;
import com.example.web_bansach.module.user.entity.Users;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private RealtimeNotificationService realtimeNotificationService;
    @Mock private PaymentEmailService paymentEmailService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void initiatePayment_shouldRejectOrderOwnedByAnotherUser() {
        Order order = order(1L, "owner@test.com", new BigDecimal("100000"));
        PaymentRequest request = paymentRequest(1L, new BigDecimal("100000"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.initiatePayment("other@test.com", request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void initiatePayment_shouldRejectNullRequest() {
        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void initiatePayment_shouldRejectInvalidOrderIdAmountAndReturnUrl() {
        PaymentRequest invalidOrderIdRequest = paymentRequest(0L, new BigDecimal("100000"));
        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", invalidOrderIdRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ID");

        PaymentRequest zeroAmountRequest = paymentRequest(1L, BigDecimal.ZERO);
        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", zeroAmountRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("So tien");

        PaymentRequest blankReturnUrlRequest = paymentRequest(1L, new BigDecimal("100000"));
        blankReturnUrlRequest.setReturnUrl(" ");
        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", blankReturnUrlRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("URL");
    }

    @Test
    void initiatePayment_shouldRejectOrderThatIsNotPending() {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        order.setStatus(OrderStatus.CONFIRMED);
        PaymentRequest request = paymentRequest(1L, new BigDecimal("100000"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("dang cho xu ly");
    }

    @Test
    void initiatePayment_shouldRejectWrongAmount() {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        PaymentRequest request = paymentRequest(1L, new BigDecimal("90000"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.initiatePayment("user@test.com", request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void initiatePayment_shouldCreatePendingPaymentWhenRequestIsValid() throws Exception {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        PaymentRequest request = paymentRequest(1L, new BigDecimal("100000"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.initiatePayment(1L, new BigDecimal("100000"), "http://return", "order 1"))
                .thenReturn("http://pay");
        when(paymentRepository.findByOrder_Id(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(99L);
            return payment;
        });

        PaymentResponse response = paymentService.initiatePayment("user@test.com", request);

        assertThat(response.getPaymentId()).isEqualTo(99L);
        assertThat(response.getPaymentUrl()).isEqualTo("http://pay");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getPaymentStatus_shouldReturnStoredPaymentStatus() {
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setStatus("SUCCESS");
        payment.setTransactionId("SEP-1");
        payment.setPaymentUrl("http://pay");

        payment.setOrder(order(1L, "user@test.com", new BigDecimal("100000")));

        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatus("user@test.com", false, 10L);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getTransactionId()).isEqualTo("SEP-1");
        assertThat(response.getPaymentUrl()).isEqualTo("http://pay");
    }

    @Test
    void getPaymentStatusByOrderId_shouldReturnPaymentForOrder() {
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setStatus("SUCCESS");
        payment.setTransactionId("SEP-1");

        payment.setOrder(order(1L, "user@test.com", new BigDecimal("100000")));

        when(paymentRepository.findByOrder_Id(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatusByOrderId("user@test.com", false, 1L);

        assertThat(response.getPaymentId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void verifyPaymentCallback_shouldAcceptSepCodeWithoutDashForLegacyPayment() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("5000"));
        payment.setTransactionId("SEP-4");

        when(paymentRepository.findByTransactionId("SEP4")).thenReturn(Optional.empty());
        when(paymentRepository.findByTransactionId("SEP-4")).thenReturn(Optional.of(payment));
        when(paymentGateway.verifyPayment("SEP-4", new BigDecimal("5000"), "Apikey test"))
                .thenReturn(true);

        boolean verified = paymentService.verifyPaymentCallback("SEP4", new BigDecimal("5000"), "Apikey test");

        assertThat(verified).isTrue();
    }

    @Test
    void getPaymentStatus_shouldRejectPaymentOwnedByAnotherUser() {
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setOrder(order(1L, "owner@test.com", new BigDecimal("100000")));

        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.getPaymentStatus("other@test.com", false, 10L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updatePaymentStatus_shouldDeductInventoryOnceWhenPaymentSucceeds() {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setTransactionId("SEP1");
        payment.setOrder(order);

        Book book = new Book();
        book.setId(20L);
        book.setTitle("Book");

        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(2);

        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(5);

        when(paymentRepository.findByTransactionId("SEP1")).thenReturn(Optional.of(payment));
        when(orderItemRepository.findByOrderIdWithBook(1L)).thenReturn(java.util.List.of(item));
        when(inventoryRepository.findByBookIdForUpdate(20L)).thenReturn(Optional.of(inventory));

        paymentService.updatePaymentStatus("SEP1", "SUCCESS", "Apikey test");
        paymentService.updatePaymentStatus("SEP1", "SUCCESS", "Apikey test");

        assertThat(inventory.getQuantity()).isEqualTo(3);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(paymentEmailService).sendPaymentSuccessEmailAfterCommit(payment);
        verify(orderItemRepository, times(1)).findByOrderIdWithBook(1L);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void updatePaymentStatus_shouldNotDeductInventoryWhenPaymentFails() {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setTransactionId("SEP1");
        payment.setOrder(order);

        when(paymentRepository.findByTransactionId("SEP1")).thenReturn(Optional.of(payment));

        paymentService.updatePaymentStatus("SEP1", "FAILED", "Apikey test");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(payment.getStatus()).isEqualTo("FAILED");
        verify(orderItemRepository, never()).findByOrderIdWithBook(1L);
        verify(paymentEmailService, never()).sendPaymentSuccessEmailAfterCommit(any(Payment.class));
    }

    @Test
    void updatePaymentStatus_shouldRejectChangingSuccessfulPaymentToFailed() {
        Order order = order(1L, "user@test.com", new BigDecimal("100000"));
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setTransactionId("SEP1");
        payment.setStatus("SUCCESS");
        payment.setOrder(order);

        when(paymentRepository.findByTransactionId("SEP1")).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.updatePaymentStatus("SEP1", "FAILED", "Apikey test"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("da ket thuc");

        assertThat(payment.getStatus()).isEqualTo("SUCCESS");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void updatePaymentStatus_shouldRejectUnknownStatus() {
        assertThatThrownBy(() -> paymentService.updatePaymentStatus("SEP1", "REFUNDED", "Apikey test"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("khong hop le");

        verify(paymentRepository, never()).findByTransactionId(any());
    }

    private Order order(Long id, String email, BigDecimal totalAmount) {
        Users user = new Users();
        user.setId(1L);
        user.setEmail(email);

        Order order = new Order();
        order.setId(id);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        return order;
    }

    private PaymentRequest paymentRequest(Long orderId, BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(amount);
        request.setReturnUrl("http://return");
        request.setDescription("order " + orderId);
        return request;
    }
}

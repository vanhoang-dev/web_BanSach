package com.example.web_bansach.module.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderStatus;
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
    @Mock private PaymentGateway paymentGateway;
    @Mock private RealtimeNotificationService realtimeNotificationService;

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

        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatus(10L);

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

        when(paymentRepository.findByOrder_Id(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatusByOrderId(1L);

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

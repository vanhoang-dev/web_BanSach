package com.example.web_bansach.module.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.web_bansach.infrastructure.email.config.MailProperties;
import com.example.web_bansach.infrastructure.email.messaging.EmailMessage;
import com.example.web_bansach.infrastructure.email.messaging.EmailPublisher;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.payment.entity.Payment;
import com.example.web_bansach.module.payment.service.email.PaymentSuccessEmailTemplate;
import com.example.web_bansach.module.payment.service.impl.PaymentEmailNotificationService;
import com.example.web_bansach.module.user.entity.Users;

@ExtendWith(MockitoExtension.class)
class PaymentEmailNotificationServiceTest {

    @Mock private EmailPublisher emailPublisher;
    @Mock private OrderItemRepository orderItemRepository;

    private PaymentEmailNotificationService service;

    @BeforeEach
    void setUp() {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setFrom("billing@bookstore.test");
        service = new PaymentEmailNotificationService(
                emailPublisher,
                orderItemRepository,
                new PaymentSuccessEmailTemplate(),
                mailProperties);
    }

    @Test
    void notifyPaymentSucceededAfterCommit_shouldPublishCompleteEmail() {
        Users user = new Users();
        user.setEmail("customer@test.com");
        user.setFullName("Khách hàng");

        Order order = new Order();
        order.setId(7L);
        order.setUser(user);
        order.setReceiverName("Nguyễn Văn A");

        Payment payment = new Payment();
        payment.setId(9L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("120000"));
        payment.setTransactionId("SEP7");
        payment.setPaidAt(LocalDateTime.of(2026, 9, 27, 10, 15));

        Book book = new Book();
        book.setTitle("Sách kiểm thử");
        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(1);
        item.setPrice(new BigDecimal("120000"));
        when(orderItemRepository.findByOrderIdWithBook(7L)).thenReturn(List.of(item));

        service.notifyPaymentSucceededAfterCommit(payment);

        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailPublisher).publishAfterCommit(messageCaptor.capture());
        EmailMessage message = messageCaptor.getValue();
        assertThat(message.getEmailType()).isEqualTo("PAYMENT_SUCCESS");
        assertThat(message.getFrom()).isEqualTo("billing@bookstore.test");
        assertThat(message.getTo()).isEqualTo("customer@test.com");
        assertThat(message.getReferenceId()).isEqualTo(7L);
        assertThat(message.getSubject()).contains("#7");
        assertThat(message.getHtmlBody()).contains("Nguyễn Văn A", "Sách kiểm thử", "SEP7");
    }

    @Test
    void notifyPaymentSucceededAfterCommit_shouldSkipWhenCustomerEmailIsMissing() {
        Payment payment = new Payment();
        payment.setOrder(new Order());

        service.notifyPaymentSucceededAfterCommit(payment);

        verify(orderItemRepository, never()).findByOrderIdWithBook(any());
        verify(emailPublisher, never()).publishAfterCommit(any());
    }
}

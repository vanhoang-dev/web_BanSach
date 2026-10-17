package com.example.web_bansach.module.payment.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.web_bansach.infrastructure.email.config.MailProperties;
import com.example.web_bansach.infrastructure.email.messaging.EmailMessage;
import com.example.web_bansach.infrastructure.email.messaging.EmailPublisher;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.payment.entity.Payment;
import com.example.web_bansach.module.payment.service.PaymentNotificationService;
import com.example.web_bansach.module.payment.service.email.PaymentSuccessEmailModel;
import com.example.web_bansach.module.payment.service.email.PaymentSuccessEmailTemplate;

@Service
public class PaymentEmailNotificationService implements PaymentNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEmailNotificationService.class);
    private static final String PAYMENT_SUCCESS_EMAIL = "PAYMENT_SUCCESS";

    private final EmailPublisher emailPublisher;
    private final OrderItemRepository orderItemRepository;
    private final PaymentSuccessEmailTemplate emailTemplate;
    private final MailProperties mailProperties;

    public PaymentEmailNotificationService(
            EmailPublisher emailPublisher,
            OrderItemRepository orderItemRepository,
            PaymentSuccessEmailTemplate emailTemplate,
            MailProperties mailProperties) {
        this.emailPublisher = emailPublisher;
        this.orderItemRepository = orderItemRepository;
        this.emailTemplate = emailTemplate;
        this.mailProperties = mailProperties;
    }

    @Override
    public void notifyPaymentSucceededAfterCommit(Payment payment) {
        Order order = payment == null ? null : payment.getOrder();
        if (order == null || order.getUser() == null || !hasText(order.getUser().getEmail())) {
            logger.warn("Skip payment success email because payment, order or customer email is missing");
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
        PaymentSuccessEmailModel model = new PaymentSuccessEmailModel(
                customerName(order),
                order.getId(),
                payment.getAmount(),
                payment.getTransactionId(),
                payment.getPaidAt(),
                orderItems.stream().map(this::toEmailItem).toList());

        EmailMessage message = EmailMessage.create(
                PAYMENT_SUCCESS_EMAIL,
                mailProperties.getFrom(),
                order.getUser().getEmail(),
                emailTemplate.subject(order.getId()),
                emailTemplate.render(model),
                order.getId());
        emailPublisher.publishAfterCommit(message);
    }

    private PaymentSuccessEmailModel.Item toEmailItem(OrderItem item) {
        String title = item.getBook() == null ? "" : item.getBook().getTitle();
        return new PaymentSuccessEmailModel.Item(title, item.getQuantity(), item.getPrice());
    }

    private String customerName(Order order) {
        if (hasText(order.getReceiverName())) {
            return order.getReceiverName();
        }
        return order.getUser().getFullName();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

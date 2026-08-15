package com.example.web_bansach.module.payment.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.web_bansach.infrastructure.messaging.email.EmailMessage;
import com.example.web_bansach.infrastructure.messaging.email.EmailQueuePublisher;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.payment.entity.Payment;

@Service
public class PaymentEmailService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEmailService.class);
    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final String PAYMENT_SUCCESS_EMAIL = "PAYMENT_SUCCESS";

    private final EmailQueuePublisher emailQueuePublisher;
    private final OrderItemRepository orderItemRepository;

    @Value("${spring.mail.username:noreply@webbansach.local}")
    private String mailFrom;

    public PaymentEmailService(EmailQueuePublisher emailQueuePublisher, OrderItemRepository orderItemRepository) {
        this.emailQueuePublisher = emailQueuePublisher;
        this.orderItemRepository = orderItemRepository;
    }

    public void sendPaymentSuccessEmailAfterCommit(Payment payment) {
        Order order = payment.getOrder();
        if (order == null || order.getUser() == null || order.getUser().getEmail() == null
                || order.getUser().getEmail().isBlank()) {
            logger.warn("Skip payment success email because order/user email is missing for payment {}", payment.getId());
            return;
        }

        Long orderId = order.getId();
        String customerEmail = order.getUser().getEmail();
        String customerName = order.getReceiverName() != null && !order.getReceiverName().isBlank()
                ? order.getReceiverName()
                : order.getUser().getFullName();
        BigDecimal amount = payment.getAmount();
        String transactionCode = payment.getTransactionId();
        LocalDateTime paidAt = payment.getPaidAt();
        List<OrderItem> items = orderItemRepository.findByOrderIdWithBook(orderId);

        EmailMessage message = EmailMessage.create(
                PAYMENT_SUCCESS_EMAIL,
                mailFrom,
                customerEmail,
                "Xac nhan thanh toan don hang #" + orderId,
                buildPaymentSuccessEmail(customerName, orderId, amount, transactionCode, paidAt, items),
                orderId);
        emailQueuePublisher.publishAfterCommit(message);
    }

    private String buildPaymentSuccessEmail(String customerName,
            Long orderId,
            BigDecimal amount,
            String transactionCode,
            LocalDateTime paidAt,
            List<OrderItem> items) {
        String displayName = customerName == null || customerName.isBlank() ? "Quy khach" : customerName;
        StringBuilder body = new StringBuilder();
        body.append("<div style=\"font-family:Arial,sans-serif;color:#222;line-height:1.5\">")
                .append("<h2>Thanh toan thanh cong</h2>")
                .append("<p>Xin chao ").append(escapeHtml(displayName)).append(",</p>")
                .append("<p>Don hang <strong>#").append(orderId)
                .append("</strong> cua ban da duoc thanh toan thanh cong bang chuyen khoan.</p>")
                .append("<table style=\"border-collapse:collapse;width:100%;max-width:640px\">")
                .append(emailRow("Ma giao dich", transactionCode))
                .append(emailRow("So tien", formatCurrency(amount)))
                .append(emailRow("Thoi gian thanh toan", paidAt != null ? paidAt.toString() : ""))
                .append("</table>");

        if (items != null && !items.isEmpty()) {
            body.append("<h3>San pham trong don</h3>")
                    .append("<table style=\"border-collapse:collapse;width:100%;max-width:640px\">")
                    .append("<tr>")
                    .append("<th style=\"border:1px solid #ddd;padding:8px;text-align:left\">Sach</th>")
                    .append("<th style=\"border:1px solid #ddd;padding:8px;text-align:right\">SL</th>")
                    .append("<th style=\"border:1px solid #ddd;padding:8px;text-align:right\">Don gia</th>")
                    .append("</tr>");
            for (OrderItem item : items) {
                body.append("<tr>")
                        .append("<td style=\"border:1px solid #ddd;padding:8px\">")
                        .append(escapeHtml(item.getBook() != null ? item.getBook().getTitle() : ""))
                        .append("</td>")
                        .append("<td style=\"border:1px solid #ddd;padding:8px;text-align:right\">")
                        .append(item.getQuantity() != null ? item.getQuantity() : 0)
                        .append("</td>")
                        .append("<td style=\"border:1px solid #ddd;padding:8px;text-align:right\">")
                        .append(formatCurrency(item.getPrice()))
                        .append("</td>")
                        .append("</tr>");
            }
            body.append("</table>");
        }

        body.append("<p>Cam on ban da mua hang tai Web Ban Sach.</p>")
                .append("</div>");
        return body.toString();
    }

    private String emailRow(String label, String value) {
        return "<tr><td style=\"border:1px solid #ddd;padding:8px;font-weight:bold\">"
                + escapeHtml(label)
                + "</td><td style=\"border:1px solid #ddd;padding:8px\">"
                + escapeHtml(value)
                + "</td></tr>";
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return NumberFormat.getCurrencyInstance(VIETNAM_LOCALE).format(amount);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

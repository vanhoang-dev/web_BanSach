package com.example.web_bansach.infrastructure.realtime;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishOrderEvent(String eventType,
            Long orderId,
            String username,
            String message,
            String status,
            Map<String, Object> data) {
        RealtimeNotification notification = new RealtimeNotification(
                eventType,
                "ORDER",
                orderId,
                "Đơn hàng",
                message,
                status,
                data,
                LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/orders", notification);

        if (username != null && !username.trim().isEmpty()) {
            messagingTemplate.convertAndSend("/topic/users/" + username.trim(), notification);
        }
    }

    public void publishPaymentEvent(String eventType,
            Long paymentId,
            Long orderId,
            String message,
            String status,
            Map<String, Object> data) {
        RealtimeNotification notification = new RealtimeNotification(
                eventType,
                "PAYMENT",
                paymentId,
                "Thanh toán",
                message,
                status,
                data,
                LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/payments", notification);

        if (orderId != null) {
            messagingTemplate.convertAndSend("/topic/orders/" + orderId, notification);
        }
    }
}
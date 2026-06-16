package com.example.web_bansach.infrastructure.realtime;

import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class RealtimeNotificationService {

    private static final long SSE_TIMEOUT = 15 * 60 * 1000L;

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> paymentOrderEmitters = new ConcurrentHashMap<>();

    public RealtimeNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public SseEmitter subscribePaymentOrder(Long orderId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        paymentOrderEmitters.computeIfAbsent(orderId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removePaymentEmitter(orderId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            removePaymentEmitter(orderId, emitter);
        });
        emitter.onError(error -> removePaymentEmitter(orderId, emitter));

        return emitter;
    }

    public void sendPaymentSnapshot(SseEmitter emitter, RealtimeNotification notification) {
        sendToEmitter(emitter, notification);
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
            publishPaymentSse(orderId, notification);
        }
    }

    private void publishPaymentSse(Long orderId, RealtimeNotification notification) {
        List<SseEmitter> emitters = paymentOrderEmitters.get(orderId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            if (!sendToEmitter(emitter, notification)) {
                removePaymentEmitter(orderId, emitter);
            }
        }
    }

    private boolean sendToEmitter(SseEmitter emitter, RealtimeNotification notification) {
        try {
            emitter.send(SseEmitter.event()
                    .name("payment")
                    .data(notification));
            return true;
        } catch (IOException | IllegalStateException ex) {
            emitter.completeWithError(ex);
            return false;
        }
    }

    private void removePaymentEmitter(Long orderId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = paymentOrderEmitters.get(orderId);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            paymentOrderEmitters.remove(orderId);
        }
    }
}

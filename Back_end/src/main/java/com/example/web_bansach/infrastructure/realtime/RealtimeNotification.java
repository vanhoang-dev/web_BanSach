package com.example.web_bansach.infrastructure.realtime;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Đóng gói dữ liệu sự kiện realtime gửi qua WebSocket hoặc SSE.
public class RealtimeNotification {
    private String eventType;
    private String entityType;
    private Long entityId;
    private String title;
    private String message;
    private String status;
    private Map<String, Object> data;
    private LocalDateTime timestamp;
}

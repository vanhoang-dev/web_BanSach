package com.example.web_bansach.infrastructure.messaging.email;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String eventId;
    private String emailType;
    private String from;
    private String to;
    private String subject;
    private String htmlBody;
    private Long referenceId;
    private LocalDateTime createdAt;

    public static EmailMessage create(String emailType,
            String from,
            String to,
            String subject,
            String htmlBody,
            Long referenceId) {
        return new EmailMessage(
                UUID.randomUUID().toString(),
                emailType,
                from,
                to,
                subject,
                htmlBody,
                referenceId,
                LocalDateTime.now());
    }
}

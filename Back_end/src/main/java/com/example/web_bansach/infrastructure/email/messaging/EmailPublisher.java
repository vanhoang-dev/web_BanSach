package com.example.web_bansach.infrastructure.email.messaging;

/**
 * Application-facing port for publishing email messages.
 * Business services do not need to know whether RabbitMQ or another transport is used.
 */
public interface EmailPublisher {

    void publishAfterCommit(EmailMessage message);
}

package com.example.web_bansach.infrastructure.messaging.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.web_bansach.infrastructure.messaging.rabbitmq.RabbitMQConfig;

@Service
public class EmailQueuePublisher {
    private static final Logger logger = LoggerFactory.getLogger(EmailQueuePublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EmailQueuePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAfterCommit(EmailMessage message) {
        Runnable publishTask = () -> publish(message);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishTask.run();
                }
            });
            return;
        }

        publishTask.run();
    }

    private void publish(EmailMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message);
            logger.info("Email message published, eventId={}, type={}, to={}, referenceId={}",
                    message.getEventId(),
                    message.getEmailType(),
                    message.getTo(),
                    message.getReferenceId());
        } catch (RuntimeException ex) {
            logger.error("Could not publish email message, type={}, to={}, referenceId={}: {}",
                    message.getEmailType(),
                    message.getTo(),
                    message.getReferenceId(),
                    ex.getMessage());
        }
    }
}

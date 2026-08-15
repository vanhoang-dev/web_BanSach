package com.example.web_bansach.infrastructure.messaging.email;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.example.web_bansach.infrastructure.external.EmailSender;
import com.example.web_bansach.infrastructure.messaging.rabbitmq.RabbitMQConfig;

@Service
@ConditionalOnProperty(name = "app.rabbitmq.email-listener.enabled", havingValue = "true", matchIfMissing = true)
public class EmailQueueConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EmailQueueConsumer.class);
    private static final int MAX_RETRY_COUNT = 3;

    private final EmailSender emailSender;
    private final RabbitTemplate rabbitTemplate;

    public EmailQueueConsumer(EmailSender emailSender, RabbitTemplate rabbitTemplate) {
        this.emailSender = emailSender;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consume(EmailMessage message, @Headers Map<String, Object> headers) {
        try {
            emailSender.sendMessage(
                    message.getFrom(),
                    message.getTo(),
                    message.getSubject(),
                    message.getHtmlBody());
            logger.info("Email sent, eventId={}, type={}, to={}, referenceId={}",
                    message.getEventId(),
                    message.getEmailType(),
                    message.getTo(),
                    message.getReferenceId());
        } catch (RuntimeException ex) {
            handleFailure(message, headers, ex);
        }
    }

    private void handleFailure(EmailMessage message, Map<String, Object> headers, RuntimeException ex) {
        int retryCount = getRetryCount(headers);
        if (retryCount >= MAX_RETRY_COUNT) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_FAILED_EXCHANGE,
                    RabbitMQConfig.EMAIL_FAILED_ROUTING_KEY,
                    message);
            logger.error("Email moved to failed queue after retries, eventId={}, type={}, to={}, reason={}",
                    message.getEventId(),
                    message.getEmailType(),
                    message.getTo(),
                    ex.getMessage());
            return;
        }

        logger.warn("Email sending failed, eventId={}, type={}, to={}, retry={}/{}, reason={}",
                message.getEventId(),
                message.getEmailType(),
                message.getTo(),
                retryCount + 1,
                MAX_RETRY_COUNT,
                ex.getMessage());
        throw new AmqpRejectAndDontRequeueException("Email sending failed", ex);
    }

    private int getRetryCount(Map<String, Object> headers) {
        Object xDeathHeader = headers.get("x-death");
        if (!(xDeathHeader instanceof List<?> deaths)) {
            return 0;
        }

        return deaths.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .filter(death -> RabbitMQConfig.EMAIL_RETRY_QUEUE.equals(death.get("queue")))
                .map(death -> death.get("count"))
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .mapToInt(Number::intValue)
                .findFirst()
                .orElse(0);
    }
}

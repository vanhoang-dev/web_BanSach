package com.example.web_bansach.infrastructure.messaging.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_EXCHANGE = "bookstore.email.exchange";
    public static final String EMAIL_RETRY_EXCHANGE = "bookstore.email.retry.exchange";
    public static final String EMAIL_FAILED_EXCHANGE = "bookstore.email.failed.exchange";

    public static final String EMAIL_QUEUE = "bookstore.email.queue";
    public static final String EMAIL_RETRY_QUEUE = "bookstore.email.retry.queue";
    public static final String EMAIL_FAILED_QUEUE = "bookstore.email.failed.queue";

    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String EMAIL_FAILED_ROUTING_KEY = "email.failed";

    private static final int EMAIL_RETRY_DELAY_MS = 30_000;

    @Bean
    public TopicExchange emailExchange() {
        return ExchangeBuilder.topicExchange(EMAIL_EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange emailRetryExchange() {
        return ExchangeBuilder.directExchange(EMAIL_RETRY_EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange emailFailedExchange() {
        return ExchangeBuilder.directExchange(EMAIL_FAILED_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .deadLetterExchange(EMAIL_RETRY_EXCHANGE)
                .deadLetterRoutingKey(EMAIL_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailRetryQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", EMAIL_RETRY_DELAY_MS);
        args.put("x-dead-letter-exchange", EMAIL_EXCHANGE);
        args.put("x-dead-letter-routing-key", EMAIL_ROUTING_KEY);
        return QueueBuilder.durable(EMAIL_RETRY_QUEUE).withArguments(args).build();
    }

    @Bean
    public Queue emailFailedQueue() {
        return QueueBuilder.durable(EMAIL_FAILED_QUEUE).build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailRetryBinding(Queue emailRetryQueue, DirectExchange emailRetryExchange) {
        return BindingBuilder.bind(emailRetryQueue).to(emailRetryExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailFailedBinding(Queue emailFailedQueue, DirectExchange emailFailedExchange) {
        return BindingBuilder.bind(emailFailedQueue).to(emailFailedExchange).with(EMAIL_FAILED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}

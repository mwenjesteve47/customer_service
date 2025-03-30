package com.example.customerservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Value("${loan.default.limit}")
    private BigDecimal defaultLoanLimit;
    @Value("${loan.limit.adjustmentPercentage}")
    private int adjustmentPercentage;

    @Value("${rabbitmq.loan.limit.exchange}")
    private String exchange;

    @Value("${rabbitmq.loan.limit.routingKey}")
    private String routingKey;
    @Value("${rabbitmq.loan.limit.queue}")
    private String queueName;
    @Value("${rabbitmq.notification.exchange}")
    private String notificationExchange;

    @Value("${rabbitmq.notification.routingKey}")
    private String notificationRoutingKey;
    @Value("${rabbitmq.notification.queue}")
    private String notificationQueue;

    @Value("${rabbitmq.loanOverdueNotification.routingKey}")
    private String loanOverdueNotificationRoutingKey;
    @Value("${rabbitmq.loanOverdueNotification.queue}")
    private String loanOverdueNotificationQueue;
    private final ConnectionFactory connectionFactory;
}

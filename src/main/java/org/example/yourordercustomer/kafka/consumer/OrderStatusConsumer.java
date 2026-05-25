package org.example.yourordercustomer.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yourordercustomer.order.service.CustomerOrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusConsumer {

    private final CustomerOrderService customerOrderService;

    @KafkaListener(topics = "#{@kafkaTopicsProperties.stockReserved}", groupId = "${spring.kafka.consumer.group-id}")
    public void onStockReserved(UUID orderId) {
        log.info("stock.reserved: orderId={}", orderId);
        customerOrderService.markOrderReserved(orderId);
    }

    @KafkaListener(topics = "#{@kafkaTopicsProperties.paymentCompleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentCompleted(UUID orderId) {
        log.info("payment.completed: orderId={}", orderId);
        customerOrderService.markOrderPaid(orderId);
    }

    @KafkaListener(topics = "#{@kafkaTopicsProperties.paymentFailed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(UUID orderId) {
        log.info("payment.failed: orderId={}", orderId);
        customerOrderService.markOrderPaymentFailed(orderId);
    }
}

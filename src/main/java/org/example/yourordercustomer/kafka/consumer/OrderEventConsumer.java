package org.example.yourordercustomer.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yourordercustomer.kafka.event.PaymentCompletedEvent;
import org.example.yourordercustomer.kafka.event.PaymentFailedEvent;
import org.example.yourordercustomer.kafka.event.StockReservedEvent;
import org.example.yourordercustomer.order.service.CustomerOrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final CustomerOrderService customerOrderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "stock.reserved", groupId = "yourorder-customer")
    public void handleStockReserved(String message) throws Exception {
        StockReservedEvent event = objectMapper.readValue(message, StockReservedEvent.class);
        log.info("Received stock.reserved event: {}", event);
        customerOrderService.markOrderReserved(event.orderId());
    }

    @KafkaListener(topics = "payment.completed", groupId = "yourorder-customer")
    public void handlePaymentCompleted(String message) throws Exception {
        PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
        log.info("Received payment.completed event: {}", event);
        customerOrderService.markOrderPaid(event.orderId());
    }

    @KafkaListener(topics = "payment.failed", groupId = "yourorder-customer")
    public void handlePaymentFailed(String message) throws Exception {
        PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
        log.info("Received payment.failed event: {}", event);
        customerOrderService.markOrderPaymentFailed(event.orderId());
    }
}

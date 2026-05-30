package org.example.yourordercustomer.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yourordercustomer.kafka.config.KafkaTopicsProperties;
import org.example.yourordercustomer.kafka.event.OrderCancelledEvent;
import org.example.yourordercustomer.kafka.event.OrderCreatedEvent;
import org.example.yourordercustomer.kafka.event.OrderItemEvent;
import org.example.yourordercustomer.order.entity.OrderEntity;
import org.example.yourordercustomer.order.entity.OrderItemEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void sendOrderCreated(OrderEntity order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                mapItems(order.getItems())
        );

        kafkaTemplate.send(
                topics.getOrderCreated(),
                order.getId().toString(),
                event
        );

        log.info("Sent order.created event: {}", event);
    }

    public void sendOrderCancelled(OrderEntity order) {
        OrderCancelledEvent event = new OrderCancelledEvent(
                order.getId(),
                order.getUserId(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                topics.getOrderCancelled(),
                order.getId().toString(),
                event
        );

        log.info("Sent order.cancelled event: {}", event);
    }

    private List<OrderItemEvent> mapItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(item -> new OrderItemEvent(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();
    }
}

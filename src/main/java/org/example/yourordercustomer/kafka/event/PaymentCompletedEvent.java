package org.example.yourordercustomer.kafka.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID orderId
) {
}
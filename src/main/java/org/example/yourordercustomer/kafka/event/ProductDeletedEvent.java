package org.example.yourordercustomer.kafka.event;

import java.util.UUID;

public record ProductDeletedEvent(
        UUID id,
        UUID companyId
) {
}

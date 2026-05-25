package org.example.yourordercustomer.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        UUID userId,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}

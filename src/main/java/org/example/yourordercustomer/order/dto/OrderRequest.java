package org.example.yourordercustomer.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequest(

        @NotNull
        UUID userId,

        @Valid
        @NotEmpty
        List<OrderItemRequest> items
) {
}

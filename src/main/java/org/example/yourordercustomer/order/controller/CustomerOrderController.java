package org.example.yourordercustomer.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.yourordercustomer.order.dto.OrderRequest;
import org.example.yourordercustomer.order.dto.OrderResponse;
import org.example.yourordercustomer.order.dto.OrderItemResponse;
import org.example.yourordercustomer.order.entity.OrderEntity;
import org.example.yourordercustomer.order.service.CustomerOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestHeader("X-User-Id") UUID userId, // из Gateway
            @Valid @RequestBody OrderRequest request
    ) {
        OrderEntity order = customerOrderService.createOrder(userId, request);
        return mapToResponse(order);
    }


    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id
    ) {
        OrderEntity order = customerOrderService.getOrderById(id, userId);
        return mapToResponse(order);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable
    ) {
        return customerOrderService.getOrders(userId, pageable)
                .map(this::mapToResponse);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id
    ) {
        OrderEntity order = customerOrderService.cancelOrder(id, userId);
        return mapToResponse(order);
    }

    @GetMapping("/check")
    public String check() {
        return "Customer service works!";
    }

    private OrderResponse mapToResponse(OrderEntity order) {
        var items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}

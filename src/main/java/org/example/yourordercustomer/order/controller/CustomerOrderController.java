package org.example.yourordercustomer.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.yourordercustomer.order.dto.OrderRequest;
import org.example.yourordercustomer.order.dto.OrderResponse;
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
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        OrderEntity order = customerOrderService.createOrder(request);
        return mapToResponse(order);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        OrderEntity order = customerOrderService.getOrderById(id);
        return mapToResponse(order);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(Pageable pageable) {
        return customerOrderService.getOrders(pageable)
                .map(this::mapToResponse);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable UUID id) {
        OrderEntity order = customerOrderService.cancelOrder(id);
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(OrderEntity order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

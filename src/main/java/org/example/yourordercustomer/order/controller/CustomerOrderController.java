package org.example.yourordercustomer.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.yourordercustomer.order.dto.OrderRequest;
import org.example.yourordercustomer.order.dto.OrderResponse;
import org.example.yourordercustomer.order.dto.OrderItemResponse;
import org.example.yourordercustomer.order.entity.OrderEntity;
import org.example.yourordercustomer.order.service.CustomerOrderService;
import org.example.yourordercustomer.security.model.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
        UUID userId = getPrincipal().userId();
        OrderEntity order = customerOrderService.createOrder(userId, request);
        return mapToResponse(order);
    }


    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        UserPrincipal principal = getPrincipal();
        OrderEntity order = isAdmin(principal)
                ? customerOrderService.getOrderById(id)
                : customerOrderService.getOrderById(id, principal.userId());
        return mapToResponse(order);
    }


    @GetMapping
    public Page<OrderResponse> getOrders(Pageable pageable) {
        UserPrincipal principal = getPrincipal();
        return (isAdmin(principal)
                ? customerOrderService.getOrders(pageable)
                : customerOrderService.getOrders(principal.userId(), pageable))
                .map(this::mapToResponse);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable UUID id) {
        UUID userId = getPrincipal().userId();
        OrderEntity order = customerOrderService.cancelOrder(id, userId);
        return mapToResponse(order);
    }


    private UserPrincipal getPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private boolean isAdmin(UserPrincipal principal) {
        return "ADMIN".equals(principal.role());
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

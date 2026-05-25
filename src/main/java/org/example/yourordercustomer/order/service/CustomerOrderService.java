package org.example.yourordercustomer.order.service;

import lombok.RequiredArgsConstructor;
import org.example.yourordercustomer.exception.NotFoundException;
import org.example.yourordercustomer.kafka.producer.OrderEventProducer;
import org.example.yourordercustomer.order.dto.OrderItemRequest;
import org.example.yourordercustomer.order.dto.OrderRequest;
import org.example.yourordercustomer.order.entity.OrderEntity;
import org.example.yourordercustomer.order.entity.OrderItemEntity;
import org.example.yourordercustomer.order.repository.OrderRepository;
import org.example.yourordercustomer.productview.entity.ProductViewEntity;
import org.example.yourordercustomer.productview.repository.ProductViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {

    private final OrderRepository orderRepository;
    private final ProductViewRepository productViewRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderEntity createOrder(UUID userId, OrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .build();

        for (OrderItemRequest itemRequest : request.items()) {
            addOrderItem(order, itemRequest);
        }

        OrderEntity savedOrder = orderRepository.save(order);
        orderEventProducer.sendOrderCreated(savedOrder);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderById(UUID orderId, UUID userId) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.getItems().forEach(item -> item.getProduct().getName());
        return order;
    }

    public OrderEntity cancelOrder(UUID orderId, UUID userId) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.getItems().forEach(item -> item.getProduct().getName());
        order.cancel();
        OrderEntity savedOrder = orderRepository.save(order);
        orderEventProducer.sendOrderCancelled(savedOrder);
        return savedOrder;
    }

    public OrderEntity markOrderReserved(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.markReserved();
        return orderRepository.save(order);
    }

    public OrderEntity markOrderPaid(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.markPaid();
        return orderRepository.save(order);
    }

    public OrderEntity markOrderPaymentFailed(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> getOrders(UUID userId, Pageable pageable) {
        Page<OrderEntity> page = orderRepository.findByUserId(userId, pageable);
        page.getContent().forEach(order ->
                order.getItems().forEach(item -> item.getProduct().getName())
        );
        return page;
    }

    private OrderEntity findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private void addOrderItem(OrderEntity order, OrderItemRequest itemRequest) {
        ProductViewEntity product = productViewRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.productId()));

        OrderItemEntity orderItem = OrderItemEntity.builder()
                .product(product)
                .quantity(itemRequest.quantity())
                .price(product.getPrice())
                .build();

        order.addItem(orderItem);
    }
}


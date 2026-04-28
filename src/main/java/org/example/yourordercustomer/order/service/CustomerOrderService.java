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

    public OrderEntity createOrder(OrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .userId(request.userId())
                .build();

        for (OrderItemRequest itemRequest : request.items()) {
            addOrderItem(order, itemRequest);
        }

        OrderEntity savedOrder = orderRepository.save(order);
        orderEventProducer.sendOrderCreated(savedOrder);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public OrderEntity cancelOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.cancel();
        OrderEntity savedOrder = orderRepository.save(order);
        orderEventProducer.sendOrderCancelled(savedOrder);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    private void addOrderItem(OrderEntity order, OrderItemRequest itemRequest) {
        ProductViewEntity product = productViewRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        OrderItemEntity orderItem = OrderItemEntity.builder()
                .product(product)
                .quantity(itemRequest.quantity())
                .price(product.getPrice())
                .build();

        order.addItem(orderItem);
    }
}

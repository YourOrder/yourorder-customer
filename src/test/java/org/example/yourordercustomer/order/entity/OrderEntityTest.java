package org.example.yourordercustomer.order.entity;

import org.example.yourordercustomer.order.status.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderEntityTest {

    @Test
    void reservedOrderCanBePaid() {
        OrderEntity order = OrderEntity.builder()
                .userId(UUID.randomUUID())
                .build();

        order.markReserved();
        order.markPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void paidOrderCannotBeCancelled() {
        OrderEntity order = OrderEntity.builder()
                .userId(UUID.randomUUID())
                .build();

        order.markReserved();
        order.markPaid();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Paid order cannot be cancelled");
    }
}

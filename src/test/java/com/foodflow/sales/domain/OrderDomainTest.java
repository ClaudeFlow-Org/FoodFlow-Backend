package com.foodflow.sales.domain;

import com.foodflow.common.domain.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDomainTest {

    // Prueba unitaria: calcula total sumando lineas de pedido (BE-UT-005)
    @Test
    void calculatesTotalFromLineItems() {
        Order order = Order.builder()
                .lineItems(List.of(
                        lineItem(1L, "Lomo saltado", "18.50", 2),
                        lineItem(2L, "Chicha", "5.00", 3)
                ))
                .build();

        order.calculateTotal();

        assertThat(order.getTotalAmount()).isEqualByComparingTo("52.00");
    }
    // fin prueba

    // Prueba unitaria: acumula cantidad cuando se repite el plato (BE-UT-006)
    @Test
    void mergesLineItemsForTheSameDish() {
        Order order = Order.builder()
                .lineItems(new ArrayList<>(List.of(lineItem(1L, "Menu", "10.00", 1))))
                .build();

        order.addLineItem(lineItem(1L, "Menu", "10.00", 2));

        assertThat(order.getLineItems()).hasSize(1);
        assertThat(order.getLineItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("30.00");
    }
    // fin prueba

    // Prueba unitaria: avanza pedido pendiente a entregado (BE-UT-007)
    @Test
    void advancesPendingOrderToDelivered() {
        Order order = Order.builder()
                .status(Order.OrderStatus.PENDIENTE)
                .build();

        order.advanceStatus();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.ENTREGADA);
        assertThat(order.isFinalState()).isTrue();
    }
    // fin prueba

    // Prueba unitaria: impide avanzar estados finales (BE-UT-008)
    @Test
    void rejectsAdvancingFinalStates() {
        Order order = Order.builder()
                .status(Order.OrderStatus.CANCELADA)
                .build();

        assertThatThrownBy(order::advanceStatus)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot advance status from CANCELADA");
    }
    // fin prueba

    // Prueba unitaria: normaliza estados externos de orden (BE-UT-009)
    @Test
    void normalizesExternalOrderStatuses() {
        assertThat(Order.OrderStatus.fromStorage("DELIVERED")).isEqualTo(Order.OrderStatus.ENTREGADA);
        assertThat(Order.OrderStatus.fromStorage("cancelled")).isEqualTo(Order.OrderStatus.CANCELADA);
        assertThat(Order.OrderStatus.fromStorage("preparing")).isEqualTo(Order.OrderStatus.PENDIENTE);
        assertThat(Order.OrderStatus.fromStorage("unknown")).isEqualTo(Order.OrderStatus.PENDIENTE);
    }
    // fin prueba

    // Prueba unitaria: genera numero de orden por usuario y secuencia (BE-UT-010)
    @Test
    void generatesOrderNumberWithUserAndSequence() {
        assertThat(Order.generateOrderNumber(1001L, 2L)).isEqualTo("1001-002");
    }
    // fin prueba

    private OrderLineItem lineItem(Long dishId, String dishName, String unitPrice, int quantity) {
        return OrderLineItem.builder()
                .dishId(dishId)
                .dishName(dishName)
                .unitPrice(new BigDecimal(unitPrice))
                .quantity(quantity)
                .build();
    }
}

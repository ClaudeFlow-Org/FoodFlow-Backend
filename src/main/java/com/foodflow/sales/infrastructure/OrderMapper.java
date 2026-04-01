package com.foodflow.sales.infrastructure;

import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toDomain(OrderJpaEntity entity) {
        List<OrderLineItem> lineItems = entity.getLineItems().stream()
                .map(this::mapLineItemToDomain)
                .collect(Collectors.toList());

        return Order.builder()
                .id(Order.OrderId.of(entity.getId()))
                .userId(entity.getUserId())
                .tableIdentifier(entity.getTableIdentifier())
                .orderDate(entity.getOrderDate())
                .lineItems(lineItems)
                .totalAmount(entity.getTotalAmount())
                .build();
    }

    public OrderJpaEntity toEntity(Order domain) {
        var builder = OrderJpaEntity.builder()
                .userId(domain.getUserId())
                .tableIdentifier(domain.getTableIdentifier())
                .orderDate(domain.getOrderDate())
                .totalAmount(domain.getTotalAmount());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        OrderJpaEntity entity = builder.build();

        if (domain.getLineItems() != null) {
            List<OrderLineItemJpaEntity> lineItemEntities = domain.getLineItems().stream()
                    .map(item -> mapLineItemToEntity(item, entity))
                    .collect(Collectors.toList());
            entity.setLineItems(lineItemEntities);
        }

        return entity;
    }

    private OrderLineItem mapLineItemToDomain(OrderLineItemJpaEntity entity) {
        return OrderLineItem.builder()
                .dishId(entity.getDishId())
                .dishName(entity.getDishName())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .build();
    }

    private OrderLineItemJpaEntity mapLineItemToEntity(OrderLineItem domain, OrderJpaEntity order) {
        return OrderLineItemJpaEntity.builder()
                .dishId(domain.getDishId())
                .dishName(domain.getDishName())
                .unitPrice(domain.getUnitPrice())
                .quantity(domain.getQuantity())
                .order(order)
                .build();
    }
}

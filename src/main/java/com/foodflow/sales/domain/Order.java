package com.foodflow.sales.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private OrderId id;
    private Long userId;
    private String tableIdentifier;
    private LocalDateTime orderDate;
    private List<OrderLineItem> lineItems;
    private BigDecimal totalAmount;

    public void calculateTotal() {
        this.totalAmount = lineItems.stream()
                .map(OrderLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addLineItem(OrderLineItem item) {
        if (this.lineItems == null) {
            this.lineItems = new ArrayList<>();
        }

        this.lineItems.stream()
                .filter(existing -> existing.getDishId().equals(item.getDishId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + item.getQuantity()),
                        () -> this.lineItems.add(item)
                );

        calculateTotal();
    }

    public record OrderId(Long value) {
        public static OrderId of(Long value) {
            return new OrderId(value);
        }

        public static OrderId empty() {
            return new OrderId(null);
        }
    }
}

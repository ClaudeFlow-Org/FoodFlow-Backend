package com.foodflow.sales.domain;

import com.foodflow.common.domain.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private OrderId id;
    private Long userId;
    private String tableIdentifier;
    private LocalDateTime orderDate;
    private List<OrderLineItem> lineItems;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String orderNumber;

    public enum OrderStatus {
        PENDIENTE,
        ENTREGADA,
        CANCELADA;

        public String toStorage() {
            return switch (this) {
                case PENDIENTE -> "PENDING";
                case ENTREGADA -> "DELIVERED";
                case CANCELADA -> "CANCELLED";
            };
        }

        public static OrderStatus fromStorage(String value) {
            if (value == null || value.isBlank()) {
                return PENDIENTE;
            }

            return switch (value.trim().toUpperCase()) {
                case "PENDIENTE", "PENDING", "PREPARING", "READY", "NEW", "OPEN", "IN_PROGRESS", "PROCESSING" -> PENDIENTE;
                case "ENTREGADA", "DELIVERED", "DONE", "COMPLETED", "SERVED", "PAID", "CLOSED" -> ENTREGADA;
                case "CANCELADA", "CANCELLED" -> CANCELADA;
                default -> PENDIENTE;
            };
        }
    }

    public Order() {
    }

    public Order(OrderId id, Long userId, String tableIdentifier, LocalDateTime orderDate,
                 List<OrderLineItem> lineItems, BigDecimal totalAmount, OrderStatus status, String orderNumber) {
        this.id = id;
        this.userId = userId;
        this.tableIdentifier = tableIdentifier;
        this.orderDate = orderDate;
        this.lineItems = lineItems;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderNumber = orderNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTableIdentifier() {
        return tableIdentifier;
    }

    public void setTableIdentifier(String tableIdentifier) {
        this.tableIdentifier = tableIdentifier;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

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

    /**
     * Advance the order status to the final delivered state.
     */
    public void advanceStatus() {
        if (this.status == null) {
            this.status = OrderStatus.PENDIENTE;
            return;
        }

        if (this.status == OrderStatus.ENTREGADA || this.status == OrderStatus.CANCELADA) {
            throw new ValidationException("Cannot advance status from " + this.status);
        }

        this.status = OrderStatus.ENTREGADA;
    }

    /**
     * Cancel the order
     */
    public void cancel() {
        if (this.status == OrderStatus.ENTREGADA) {
            throw new ValidationException("Cannot cancel a delivered order");
        }
        if (this.status == OrderStatus.CANCELADA) {
            throw new ValidationException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELADA;
    }

    /**
     * Check if the order is in a final state.
     */
    public boolean isFinalState() {
        return this.status == OrderStatus.ENTREGADA || this.status == OrderStatus.CANCELADA;
    }

    /**
     * Generate a unique order number for a user based on their sequence
     * Format: {userId}-{sequenceNumber} (e.g., 1001-001, 1001-002)
     */
    public static String generateOrderNumber(Long userId, Long sequenceNumber) {
        return String.format("%d-%03d", userId, sequenceNumber);
    }

    public record OrderId(Long value) {
        public static OrderId of(Long value) {
            return new OrderId(value);
        }

        public static OrderId empty() {
            return new OrderId(null);
        }
    }

    public static class Builder {
        private OrderId id;
        private Long userId;
        private String tableIdentifier;
        private LocalDateTime orderDate;
        private List<OrderLineItem> lineItems;
        private BigDecimal totalAmount;
        private OrderStatus status;
        private String orderNumber;

        public Builder id(OrderId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder tableIdentifier(String tableIdentifier) {
            this.tableIdentifier = tableIdentifier;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder lineItems(List<OrderLineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Order build() {
            return new Order(id, userId, tableIdentifier, orderDate, lineItems, totalAmount, status, orderNumber);
        }
    }
}

package com.foodflow.sales.application;

import com.foodflow.sales.domain.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String tableIdentifier;
    private LocalDateTime orderDate;
    private List<OrderLineItemResponse> lineItems;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String orderNumber, String tableIdentifier, LocalDateTime orderDate,
                         List<OrderLineItemResponse> lineItems, BigDecimal totalAmount, Order.OrderStatus status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.tableIdentifier = tableIdentifier;
        this.orderDate = orderDate;
        this.lineItems = lineItems;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public List<OrderLineItemResponse> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItemResponse> lineItems) {
        this.lineItems = lineItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public static class Builder {
        private Long id;
        private String orderNumber;
        private String tableIdentifier;
        private LocalDateTime orderDate;
        private List<OrderLineItemResponse> lineItems;
        private BigDecimal totalAmount;
        private Order.OrderStatus status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
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

        public Builder lineItems(List<OrderLineItemResponse> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder status(Order.OrderStatus status) {
            this.status = status;
            return this;
        }

        public OrderResponse build() {
            return new OrderResponse(id, orderNumber, tableIdentifier, orderDate, lineItems, totalAmount, status);
        }
    }
}

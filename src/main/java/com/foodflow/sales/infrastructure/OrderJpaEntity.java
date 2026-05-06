package com.foodflow.sales.infrastructure;

import com.foodflow.sales.domain.Order;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "table_identifier", nullable = false, length = 50)
    private String tableIdentifier;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 20)
    private String status = Order.OrderStatus.PENDIENTE.toStorage();

    @Column(name = "order_number", length = 20)
    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItemJpaEntity> lineItems = new ArrayList<>();

    public OrderJpaEntity() {
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderLineItemJpaEntity> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItemJpaEntity> lineItems) {
        this.lineItems = lineItems;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String tableIdentifier;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private String status = Order.OrderStatus.PENDIENTE.toStorage();
        private String orderNumber;
        private List<OrderLineItemJpaEntity> lineItems = new ArrayList<>();

        public Builder id(Long id) {
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

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder lineItems(List<OrderLineItemJpaEntity> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public OrderJpaEntity build() {
            OrderJpaEntity entity = new OrderJpaEntity();
            entity.id = this.id;
            entity.userId = this.userId;
            entity.tableIdentifier = this.tableIdentifier;
            entity.orderDate = this.orderDate;
            entity.totalAmount = this.totalAmount;
            entity.status = this.status;
            entity.orderNumber = this.orderNumber;
            entity.lineItems = this.lineItems;
            return entity;
        }
    }
}

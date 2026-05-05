package com.foodflow.sales.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderLineItem {

    private Long dishId;
    private String dishName;
    private BigDecimal unitPrice;
    private Integer quantity;

    public OrderLineItem() {
    }

    public OrderLineItem(Long dishId, String dishName, BigDecimal unitPrice, Integer quantity) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return Objects.equals(dishId, that.dishId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId);
    }

    public static class Builder {
        private Long dishId;
        private String dishName;
        private BigDecimal unitPrice;
        private Integer quantity;

        public Builder dishId(Long dishId) {
            this.dishId = dishId;
            return this;
        }

        public Builder dishName(String dishName) {
            this.dishName = dishName;
            return this;
        }

        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderLineItem build() {
            return new OrderLineItem(dishId, dishName, unitPrice, quantity);
        }
    }
}

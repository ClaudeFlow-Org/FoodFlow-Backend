package com.foodflow.finance.application;

import java.math.BigDecimal;

public class TopDishResponse {

    private Long dishId;
    private String dishName;
    private Integer quantitySold;
    private BigDecimal totalRevenue;

    public TopDishResponse() {
    }

    public TopDishResponse(Long dishId, String dishName, Integer quantitySold, BigDecimal totalRevenue) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    public static Builder builder() {
        return new Builder();
    }

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

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public static class Builder {
        private Long dishId;
        private String dishName;
        private Integer quantitySold;
        private BigDecimal totalRevenue;

        public Builder dishId(Long dishId) {
            this.dishId = dishId;
            return this;
        }

        public Builder dishName(String dishName) {
            this.dishName = dishName;
            return this;
        }

        public Builder quantitySold(Integer quantitySold) {
            this.quantitySold = quantitySold;
            return this;
        }

        public Builder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public TopDishResponse build() {
            return new TopDishResponse(dishId, dishName, quantitySold, totalRevenue);
        }
    }
}

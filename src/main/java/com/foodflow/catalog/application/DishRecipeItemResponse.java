package com.foodflow.catalog.application;

import java.math.BigDecimal;

public class DishRecipeItemResponse {

    private Long productId;
    private String productName;
    private BigDecimal requiredQuantity;
    private String requiredUnitOfMeasure;
    private String unitOfMeasure;
    private String stockUnitOfMeasure;
    private BigDecimal stockLevel;
    private Integer availableOrders;
    private BigDecimal missingForOneOrder;

    public DishRecipeItemResponse() {
    }

    public DishRecipeItemResponse(Long productId, String productName, BigDecimal requiredQuantity,
                                  String unitOfMeasure, BigDecimal stockLevel, Integer availableOrders,
                                  BigDecimal missingForOneOrder) {
        this(productId, productName, requiredQuantity, unitOfMeasure, unitOfMeasure, unitOfMeasure,
                stockLevel, availableOrders, missingForOneOrder);
    }

    public DishRecipeItemResponse(Long productId, String productName, BigDecimal requiredQuantity,
                                  String requiredUnitOfMeasure, String unitOfMeasure, String stockUnitOfMeasure,
                                  BigDecimal stockLevel, Integer availableOrders,
                                  BigDecimal missingForOneOrder) {
        this.productId = productId;
        this.productName = productName;
        this.requiredQuantity = requiredQuantity;
        this.requiredUnitOfMeasure = requiredUnitOfMeasure;
        this.unitOfMeasure = unitOfMeasure;
        this.stockUnitOfMeasure = stockUnitOfMeasure;
        this.stockLevel = stockLevel;
        this.availableOrders = availableOrders;
        this.missingForOneOrder = missingForOneOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(BigDecimal requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getRequiredUnitOfMeasure() {
        return requiredUnitOfMeasure;
    }

    public void setRequiredUnitOfMeasure(String requiredUnitOfMeasure) {
        this.requiredUnitOfMeasure = requiredUnitOfMeasure;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getStockUnitOfMeasure() {
        return stockUnitOfMeasure;
    }

    public void setStockUnitOfMeasure(String stockUnitOfMeasure) {
        this.stockUnitOfMeasure = stockUnitOfMeasure;
    }

    public BigDecimal getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(BigDecimal stockLevel) {
        this.stockLevel = stockLevel;
    }

    public Integer getAvailableOrders() {
        return availableOrders;
    }

    public void setAvailableOrders(Integer availableOrders) {
        this.availableOrders = availableOrders;
    }

    public BigDecimal getMissingForOneOrder() {
        return missingForOneOrder;
    }

    public void setMissingForOneOrder(BigDecimal missingForOneOrder) {
        this.missingForOneOrder = missingForOneOrder;
    }

    public static class Builder {
        private Long productId;
        private String productName;
        private BigDecimal requiredQuantity;
        private String requiredUnitOfMeasure;
        private String unitOfMeasure;
        private String stockUnitOfMeasure;
        private BigDecimal stockLevel;
        private Integer availableOrders;
        private BigDecimal missingForOneOrder;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder requiredQuantity(BigDecimal requiredQuantity) {
            this.requiredQuantity = requiredQuantity;
            return this;
        }

        public Builder requiredUnitOfMeasure(String requiredUnitOfMeasure) {
            this.requiredUnitOfMeasure = requiredUnitOfMeasure;
            return this;
        }

        public Builder unitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        public Builder stockUnitOfMeasure(String stockUnitOfMeasure) {
            this.stockUnitOfMeasure = stockUnitOfMeasure;
            return this;
        }

        public Builder stockLevel(BigDecimal stockLevel) {
            this.stockLevel = stockLevel;
            return this;
        }

        public Builder availableOrders(Integer availableOrders) {
            this.availableOrders = availableOrders;
            return this;
        }

        public Builder missingForOneOrder(BigDecimal missingForOneOrder) {
            this.missingForOneOrder = missingForOneOrder;
            return this;
        }

        public DishRecipeItemResponse build() {
            return new DishRecipeItemResponse(productId, productName, requiredQuantity, requiredUnitOfMeasure,
                    unitOfMeasure, stockUnitOfMeasure, stockLevel, availableOrders, missingForOneOrder);
        }
    }
}

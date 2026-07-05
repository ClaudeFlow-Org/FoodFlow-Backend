package com.foodflow.catalog.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DishRecipeItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Required quantity is required")
    @DecimalMin(value = "0.001", message = "Required quantity must be greater than 0")
    private BigDecimal requiredQuantity;

    private String requiredUnitOfMeasure;

    public DishRecipeItemRequest() {
    }

    public DishRecipeItemRequest(Long productId, BigDecimal requiredQuantity) {
        this(productId, requiredQuantity, null);
    }

    public DishRecipeItemRequest(Long productId, BigDecimal requiredQuantity, String requiredUnitOfMeasure) {
        this.productId = productId;
        this.requiredQuantity = requiredQuantity;
        this.requiredUnitOfMeasure = requiredUnitOfMeasure;
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

    public static class Builder {
        private Long productId;
        private BigDecimal requiredQuantity;
        private String requiredUnitOfMeasure;

        public Builder productId(Long productId) {
            this.productId = productId;
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

        public DishRecipeItemRequest build() {
            return new DishRecipeItemRequest(productId, requiredQuantity, requiredUnitOfMeasure);
        }
    }
}

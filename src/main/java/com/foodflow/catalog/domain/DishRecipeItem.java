package com.foodflow.catalog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DishRecipeItem {

    private DishRecipeItemId id;
    private Long userId;
    private Long dishId;
    private Long productId;
    private BigDecimal requiredQuantity;
    private String requiredUnitOfMeasure;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DishRecipeItem() {
    }

    public DishRecipeItem(DishRecipeItemId id, Long userId, Long dishId, Long productId,
                          BigDecimal requiredQuantity, String requiredUnitOfMeasure,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.dishId = dishId;
        this.productId = productId;
        this.requiredQuantity = requiredQuantity;
        this.requiredUnitOfMeasure = requiredUnitOfMeasure;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public DishRecipeItemId getId() {
        return id;
    }

    public void setId(DishRecipeItemId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public record DishRecipeItemId(Long value) {
        public static DishRecipeItemId of(Long value) {
            return new DishRecipeItemId(value);
        }

        public static DishRecipeItemId empty() {
            return new DishRecipeItemId(null);
        }
    }

    public static class Builder {
        private DishRecipeItemId id;
        private Long userId;
        private Long dishId;
        private Long productId;
        private BigDecimal requiredQuantity;
        private String requiredUnitOfMeasure;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(DishRecipeItemId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder dishId(Long dishId) {
            this.dishId = dishId;
            return this;
        }

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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DishRecipeItem build() {
            return new DishRecipeItem(id, userId, dishId, productId, requiredQuantity, requiredUnitOfMeasure, createdAt, updatedAt);
        }
    }
}

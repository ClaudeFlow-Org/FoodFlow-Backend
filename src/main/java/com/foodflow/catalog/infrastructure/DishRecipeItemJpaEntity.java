package com.foodflow.catalog.infrastructure;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "dish_recipe_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_dish_recipe_product", columnNames = {"dish_id", "product_id"})
        }
)
public class DishRecipeItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "dish_id", nullable = false)
    private Long dishId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "required_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal requiredQuantity;

    @Column(name = "required_unit_of_measure")
    private String requiredUnitOfMeasure;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public DishRecipeItemJpaEntity() {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long dishId;
        private Long productId;
        private BigDecimal requiredQuantity;
        private String requiredUnitOfMeasure;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
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

        public DishRecipeItemJpaEntity build() {
            DishRecipeItemJpaEntity entity = new DishRecipeItemJpaEntity();
            entity.id = this.id;
            entity.userId = this.userId;
            entity.dishId = this.dishId;
            entity.productId = this.productId;
            entity.requiredQuantity = this.requiredQuantity;
            entity.requiredUnitOfMeasure = this.requiredUnitOfMeasure;
            entity.createdAt = this.createdAt;
            entity.updatedAt = this.updatedAt;
            return entity;
        }
    }
}

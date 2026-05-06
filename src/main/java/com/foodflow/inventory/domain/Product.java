package com.foodflow.inventory.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

    private ProductId id;
    private String name;
    private String description;
    private String category;
    private String supplier;
    private BigDecimal lowStockThreshold;
    private BigDecimal stockLevel;
    private BigDecimal unitCost;
    private String unitOfMeasure;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(ProductId id, String name, String description, String category, String supplier,
                   BigDecimal lowStockThreshold, BigDecimal stockLevel, BigDecimal unitCost, String unitOfMeasure,
                   Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.supplier = supplier;
        this.lowStockThreshold = lowStockThreshold;
        this.stockLevel = stockLevel;
        this.unitCost = unitCost;
        this.unitOfMeasure = unitOfMeasure;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public ProductId getId() {
        return id;
    }

    public void setId(ProductId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(BigDecimal lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public BigDecimal getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(BigDecimal stockLevel) {
        this.stockLevel = stockLevel;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public void updateDetails(String name, String description, String category,
                             String supplier, BigDecimal stockLevel,
                             BigDecimal unitCost, BigDecimal lowStockThreshold,
                             String unitOfMeasure) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (category != null) {
            this.category = category;
        }
        if (supplier != null) {
            this.supplier = supplier;
        }
        if (stockLevel != null && stockLevel.compareTo(BigDecimal.ZERO) >= 0) {
            this.stockLevel = stockLevel;
        }
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) >= 0) {
            this.unitCost = unitCost;
        }
        if (lowStockThreshold != null && lowStockThreshold.compareTo(BigDecimal.ZERO) >= 0) {
            this.lowStockThreshold = lowStockThreshold;
        }
        if (unitOfMeasure != null && !unitOfMeasure.isBlank()) {
            this.unitOfMeasure = unitOfMeasure;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public record ProductId(Long value) {
        public static ProductId of(Long value) {
            return new ProductId(value);
        }

        public static ProductId empty() {
            return new ProductId(null);
        }
    }

    public static class Builder {
        private ProductId id;
        private String name;
        private String description;
        private String category;
        private String supplier;
        private BigDecimal lowStockThreshold;
        private BigDecimal stockLevel;
        private BigDecimal unitCost;
        private String unitOfMeasure;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(ProductId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder supplier(String supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder lowStockThreshold(BigDecimal lowStockThreshold) {
            this.lowStockThreshold = lowStockThreshold;
            return this;
        }

        public Builder stockLevel(BigDecimal stockLevel) {
            this.stockLevel = stockLevel;
            return this;
        }

        public Builder unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Builder unitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
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

        public Product build() {
            return new Product(id, name, description, category, supplier, lowStockThreshold, stockLevel,
                            unitCost, unitOfMeasure, userId, createdAt, updatedAt);
        }
    }
}

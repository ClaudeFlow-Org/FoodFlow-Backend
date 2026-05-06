package com.foodflow.inventory.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String supplier;
    private BigDecimal lowStockThreshold;
    private BigDecimal stockLevel;
    private BigDecimal unitCost;
    private String unitOfMeasure;
    private LocalDateTime createdAt;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, String category, String supplier,
                          BigDecimal lowStockThreshold, BigDecimal stockLevel, BigDecimal unitCost,
                          String unitOfMeasure, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.supplier = supplier;
        this.lowStockThreshold = lowStockThreshold;
        this.stockLevel = stockLevel;
        this.unitCost = unitCost;
        this.unitOfMeasure = unitOfMeasure;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String supplier;
        private BigDecimal lowStockThreshold;
        private BigDecimal stockLevel;
        private BigDecimal unitCost;
        private String unitOfMeasure;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProductResponse build() {
            return new ProductResponse(id, name, description, category, supplier, lowStockThreshold,
                    stockLevel, unitCost, unitOfMeasure, createdAt);
        }
    }
}

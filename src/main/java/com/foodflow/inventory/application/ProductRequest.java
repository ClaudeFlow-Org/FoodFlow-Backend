package com.foodflow.inventory.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String category; // String to receive from frontend, mapped to enum

    @Size(max = 200, message = "Supplier must not exceed 200 characters")
    private String supplier;

    @NotNull(message = "Stock level is required")
    @DecimalMin(value = "0.0", message = "Stock level must be non-negative")
    private BigDecimal stockLevel;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.01", message = "Unit cost must be greater than 0")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0", message = "Low stock threshold must be non-negative")
    private BigDecimal lowStockThreshold;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20, message = "Unit of measure must not exceed 20 characters")
    private String unitOfMeasure;

    public ProductRequest() {
    }

    public static Builder builder() {
        return new Builder();
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

    public BigDecimal getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(BigDecimal lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public static class Builder {
        private String name;
        private String description;
        private String category;
        private String supplier;
        private BigDecimal stockLevel;
        private BigDecimal unitCost;
        private BigDecimal lowStockThreshold;
        private String unitOfMeasure;

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

        public Builder stockLevel(BigDecimal stockLevel) {
            this.stockLevel = stockLevel;
            return this;
        }

        public Builder unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Builder lowStockThreshold(BigDecimal lowStockThreshold) {
            this.lowStockThreshold = lowStockThreshold;
            return this;
        }

        public Builder unitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        public ProductRequest build() {
            ProductRequest request = new ProductRequest();
            request.name = this.name;
            request.description = this.description;
            request.category = this.category;
            request.supplier = this.supplier;
            request.stockLevel = this.stockLevel;
            request.unitCost = this.unitCost;
            request.lowStockThreshold = this.lowStockThreshold;
            request.unitOfMeasure = this.unitOfMeasure;
            return request;
        }
    }
}

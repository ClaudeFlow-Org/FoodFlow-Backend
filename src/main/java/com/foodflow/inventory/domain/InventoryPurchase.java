package com.foodflow.inventory.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryPurchase {

    private InventoryPurchaseId id;
    private Long userId;
    private Long productId;
    private String productName;
    private String category;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private LocalDateTime purchasedAt;

    public InventoryPurchase() {
    }

    public InventoryPurchase(InventoryPurchaseId id, Long userId, Long productId, String productName,
                             String category, BigDecimal quantity, BigDecimal unitCost,
                             BigDecimal totalCost, LocalDateTime purchasedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.purchasedAt = purchasedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public InventoryPurchaseId getId() {
        return id;
    }

    public void setId(InventoryPurchaseId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }

    public record InventoryPurchaseId(Long value) {
        public static InventoryPurchaseId of(Long value) {
            return new InventoryPurchaseId(value);
        }

        public static InventoryPurchaseId empty() {
            return new InventoryPurchaseId(null);
        }
    }

    public static class Builder {
        private InventoryPurchaseId id;
        private Long userId;
        private Long productId;
        private String productName;
        private String category;
        private BigDecimal quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private LocalDateTime purchasedAt;

        public Builder id(InventoryPurchaseId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Builder totalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public Builder purchasedAt(LocalDateTime purchasedAt) {
            this.purchasedAt = purchasedAt;
            return this;
        }

        public InventoryPurchase build() {
            return new InventoryPurchase(id, userId, productId, productName, category, quantity, unitCost, totalCost, purchasedAt);
        }
    }
}

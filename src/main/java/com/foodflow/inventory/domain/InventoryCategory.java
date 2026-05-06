package com.foodflow.inventory.domain;

import java.time.LocalDateTime;

public class InventoryCategory {

    private InventoryCategoryId id;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryCategory() {
    }

    public InventoryCategory(InventoryCategoryId id, Long userId, String name,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public InventoryCategoryId getId() {
        return id;
    }

    public void setId(InventoryCategoryId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public record InventoryCategoryId(Long value) {
        public static InventoryCategoryId of(Long value) {
            return new InventoryCategoryId(value);
        }

        public static InventoryCategoryId empty() {
            return new InventoryCategoryId(null);
        }
    }

    public static class Builder {
        private InventoryCategoryId id;
        private Long userId;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(InventoryCategoryId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public InventoryCategory build() {
            return new InventoryCategory(id, userId, name, createdAt, updatedAt);
        }
    }
}

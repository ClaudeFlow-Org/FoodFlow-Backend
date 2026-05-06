package com.foodflow.inventory.application;

import java.time.LocalDateTime;

public class InventoryCategoryResponse {

    private Long id;
    private String name;
    private String value;
    private String label;
    private LocalDateTime createdAt;

    public InventoryCategoryResponse() {
    }

    public InventoryCategoryResponse(Long id, String name, String value, String label, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.label = label;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        private String value;
        private String label;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryCategoryResponse build() {
            return new InventoryCategoryResponse(id, name, value, label, createdAt);
        }
    }
}

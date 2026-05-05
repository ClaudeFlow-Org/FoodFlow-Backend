package com.foodflow.catalog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Dish {

    private DishId id;
    private String name;
    private String description;
    private BigDecimal price;
    private String ingredients;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Dish() {
    }

    public Dish(DishId id, String name, String description, BigDecimal price, String ingredients,
                Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public DishId getId() {
        return id;
    }

    public void setId(DishId id) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
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

    public void updateDetails(String name, String description, BigDecimal price, String ingredients) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            this.price = price;
        }
        if (ingredients != null) {
            this.ingredients = ingredients;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public record DishId(Long value) {
        public static DishId of(Long value) {
            return new DishId(value);
        }

        public static DishId empty() {
            return new DishId(null);
        }
    }

    public static class Builder {
        private DishId id;
        private String name;
        private String description;
        private BigDecimal price;
        private String ingredients;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(DishId id) {
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

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder ingredients(String ingredients) {
            this.ingredients = ingredients;
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

        public Dish build() {
            return new Dish(id, name, description, price, ingredients, userId, createdAt, updatedAt);
        }
    }
}

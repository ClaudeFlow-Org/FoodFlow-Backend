package com.foodflow.catalog.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DishResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String ingredients;
    private List<DishRecipeItemResponse> recipeItems;
    private Integer availableOrders;
    private LocalDateTime createdAt;

    public DishResponse() {
    }

    public DishResponse(Long id, String name, String description, BigDecimal price, String ingredients, LocalDateTime createdAt) {
        this(id, name, description, price, ingredients, List.of(), null, createdAt);
    }

    public DishResponse(Long id, String name, String description, BigDecimal price, String ingredients,
                        List<DishRecipeItemResponse> recipeItems, Integer availableOrders, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.recipeItems = recipeItems;
        this.availableOrders = availableOrders;
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

    public List<DishRecipeItemResponse> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(List<DishRecipeItemResponse> recipeItems) {
        this.recipeItems = recipeItems;
    }

    public Integer getAvailableOrders() {
        return availableOrders;
    }

    public void setAvailableOrders(Integer availableOrders) {
        this.availableOrders = availableOrders;
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
        private BigDecimal price;
        private String ingredients;
        private List<DishRecipeItemResponse> recipeItems = List.of();
        private Integer availableOrders;
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

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder ingredients(String ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public Builder recipeItems(List<DishRecipeItemResponse> recipeItems) {
            this.recipeItems = recipeItems;
            return this;
        }

        public Builder availableOrders(Integer availableOrders) {
            this.availableOrders = availableOrders;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DishResponse build() {
            return new DishResponse(id, name, description, price, ingredients, recipeItems, availableOrders, createdAt);
        }
    }
}

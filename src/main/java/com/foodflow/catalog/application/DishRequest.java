package com.foodflow.catalog.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class DishRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Size(max = 500, message = "Ingredients must not exceed 500 characters")
    private String ingredients;

    @Valid
    private List<DishRecipeItemRequest> recipeItems;

    public DishRequest() {
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

    public List<DishRecipeItemRequest> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(List<DishRecipeItemRequest> recipeItems) {
        this.recipeItems = recipeItems;
    }

    public static class Builder {
        private String name;
        private String description;
        private BigDecimal price;
        private String ingredients;
        private List<DishRecipeItemRequest> recipeItems;

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

        public Builder recipeItems(List<DishRecipeItemRequest> recipeItems) {
            this.recipeItems = recipeItems;
            return this;
        }

        public DishRequest build() {
            DishRequest request = new DishRequest();
            request.name = this.name;
            request.description = this.description;
            request.price = this.price;
            request.ingredients = this.ingredients;
            request.recipeItems = this.recipeItems;
            return request;
        }
    }
}

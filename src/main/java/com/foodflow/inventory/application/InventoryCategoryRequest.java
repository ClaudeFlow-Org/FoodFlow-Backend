package com.foodflow.inventory.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InventoryCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 80, message = "Category name must not exceed 80 characters")
    private String name;

    public InventoryCategoryRequest() {
    }

    public InventoryCategoryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

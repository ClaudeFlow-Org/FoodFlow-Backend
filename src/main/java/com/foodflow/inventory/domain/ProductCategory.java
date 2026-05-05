package com.foodflow.inventory.domain;

/**
 * Predefined product categories for inventory management
 */
public enum ProductCategory {

    MEAT("Carnes", "Meat"),
    VEGETABLES("Vegetales", "Vegetables"),
    DAIRY("Lácteos", "Dairy"),
    BEVERAGES("Bebidas", "Beverages"),
    BAKERY("Panadería", "Bakery"),
    CONDIMENTS("Condimentos", "Condiments"),
    GRAINS("Granos", "Grains"),
    SNACKS("Snacks", "Snacks"),
    OTHER("Otro", "Other");

    private final String displayNameEs;
    private final String displayNameEn;

    ProductCategory(String displayNameEs, String displayNameEn) {
        this.displayNameEs = displayNameEs;
        this.displayNameEn = displayNameEn;
    }

    public String getDisplayName() {
        return displayNameEs; // Default to Spanish
    }

    public String getDisplayNameEs() {
        return displayNameEs;
    }

    public String getDisplayNameEn() {
        return displayNameEn;
    }

    /**
     * Convert string to ProductCategory, case-insensitive
     */
    public static ProductCategory fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER;
        }
        try {
            return ProductCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}

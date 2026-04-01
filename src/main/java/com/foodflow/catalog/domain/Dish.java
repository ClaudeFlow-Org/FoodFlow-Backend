package com.foodflow.catalog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish {

    private DishId id;
    private String name;
    private String description;
    private BigDecimal price;
    private String ingredients;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
}

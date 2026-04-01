package com.foodflow.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private ProductId id;
    private String name;
    private BigDecimal stockLevel;
    private BigDecimal unitCost;
    private String unitOfMeasure;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateDetails(String name, BigDecimal stockLevel, BigDecimal unitCost, String unitOfMeasure) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (stockLevel != null && stockLevel.compareTo(BigDecimal.ZERO) >= 0) {
            this.stockLevel = stockLevel;
        }
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) >= 0) {
            this.unitCost = unitCost;
        }
        if (unitOfMeasure != null && !unitOfMeasure.isBlank()) {
            this.unitOfMeasure = unitOfMeasure;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public record ProductId(Long value) {
        public static ProductId of(Long value) {
            return new ProductId(value);
        }

        public static ProductId empty() {
            return new ProductId(null);
        }
    }
}

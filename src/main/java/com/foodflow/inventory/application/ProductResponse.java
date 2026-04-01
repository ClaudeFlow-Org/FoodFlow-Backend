package com.foodflow.inventory.application;

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
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal stockLevel;
    private BigDecimal unitCost;
    private String unitOfMeasure;
    private LocalDateTime createdAt;
}

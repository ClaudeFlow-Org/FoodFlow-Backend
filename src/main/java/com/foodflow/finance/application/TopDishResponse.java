package com.foodflow.finance.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDishResponse {

    private Long dishId;
    private String dishName;
    private Integer quantitySold;
    private BigDecimal totalRevenue;
}

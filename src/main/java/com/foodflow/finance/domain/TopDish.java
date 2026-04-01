package com.foodflow.finance.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDish {

    private Long dishId;
    private String dishName;
    private Integer quantitySold;
    private BigDecimal totalRevenue;
}

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
public class ExpenseCategory {

    private String name;
    private BigDecimal amount;
    private BigDecimal percentage;

    public void calculatePercentage(BigDecimal totalExpenses) {
        if (totalExpenses != null && totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            this.percentage = this.amount
                    .divide(totalExpenses, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}

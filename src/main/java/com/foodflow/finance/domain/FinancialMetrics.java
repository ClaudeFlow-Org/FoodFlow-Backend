package com.foodflow.finance.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialMetrics {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Double incomeVariation;
    private Double expensesVariation;

    public void calculateVariations(FinancialMetrics previous) {
        if (previous != null && previous.totalIncome != null && previous.totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            this.incomeVariation = calculateVariationPercentage(this.totalIncome, previous.totalIncome);
        }
        if (previous != null && previous.totalExpenses != null && previous.totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            this.expensesVariation = calculateVariationPercentage(this.totalExpenses, previous.totalExpenses);
        }
    }

    private Double calculateVariationPercentage(BigDecimal current, BigDecimal previous) {
        return current.subtract(previous)
                .divide(previous, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}

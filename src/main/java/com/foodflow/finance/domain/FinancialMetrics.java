package com.foodflow.finance.domain;

import java.math.BigDecimal;

public class FinancialMetrics {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Double incomeVariation;
    private Double expensesVariation;

    public FinancialMetrics() {
    }

    public FinancialMetrics(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netProfit,
                            Double incomeVariation, Double expensesVariation) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
        this.incomeVariation = incomeVariation;
        this.expensesVariation = expensesVariation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public Double getIncomeVariation() {
        return incomeVariation;
    }

    public void setIncomeVariation(Double incomeVariation) {
        this.incomeVariation = incomeVariation;
    }

    public Double getExpensesVariation() {
        return expensesVariation;
    }

    public void setExpensesVariation(Double expensesVariation) {
        this.expensesVariation = expensesVariation;
    }

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

    public static class Builder {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private Double incomeVariation;
        private Double expensesVariation;

        public Builder totalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
            return this;
        }

        public Builder totalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public Builder netProfit(BigDecimal netProfit) {
            this.netProfit = netProfit;
            return this;
        }

        public Builder incomeVariation(Double incomeVariation) {
            this.incomeVariation = incomeVariation;
            return this;
        }

        public Builder expensesVariation(Double expensesVariation) {
            this.expensesVariation = expensesVariation;
            return this;
        }

        public FinancialMetrics build() {
            return new FinancialMetrics(totalIncome, totalExpenses, netProfit, incomeVariation, expensesVariation);
        }
    }
}

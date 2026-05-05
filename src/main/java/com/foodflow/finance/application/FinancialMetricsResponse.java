package com.foodflow.finance.application;

import java.math.BigDecimal;

public class FinancialMetricsResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Double incomeVariation;
    private Double expensesVariation;

    public FinancialMetricsResponse() {
    }

    public FinancialMetricsResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netProfit,
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

        public FinancialMetricsResponse build() {
            return new FinancialMetricsResponse(totalIncome, totalExpenses, netProfit, incomeVariation, expensesVariation);
        }
    }
}

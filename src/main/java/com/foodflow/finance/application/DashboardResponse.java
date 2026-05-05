package com.foodflow.finance.application;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Double incomeVariation;
    private Double expensesVariation;
    private List<TopDishResponse> top5Dishes;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netProfit,
                            Double incomeVariation, Double expensesVariation, List<TopDishResponse> top5Dishes) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
        this.incomeVariation = incomeVariation;
        this.expensesVariation = expensesVariation;
        this.top5Dishes = top5Dishes;
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

    public List<TopDishResponse> getTop5Dishes() {
        return top5Dishes;
    }

    public void setTop5Dishes(List<TopDishResponse> top5Dishes) {
        this.top5Dishes = top5Dishes;
    }

    public static class Builder {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private Double incomeVariation;
        private Double expensesVariation;
        private List<TopDishResponse> top5Dishes;

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

        public Builder top5Dishes(List<TopDishResponse> top5Dishes) {
            this.top5Dishes = top5Dishes;
            return this;
        }

        public DashboardResponse build() {
            return new DashboardResponse(totalIncome, totalExpenses, netProfit, incomeVariation, expensesVariation, top5Dishes);
        }
    }
}

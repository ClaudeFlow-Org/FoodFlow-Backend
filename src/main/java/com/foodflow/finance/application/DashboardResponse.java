package com.foodflow.finance.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardResponse {

    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private Double incomeVariation;
    private Double expensesVariation;
    private Long orderCount;
    private List<TopDishResponse> top5Dishes;

    public DashboardResponse() {
    }

    public DashboardResponse(String period, LocalDateTime startDate, LocalDateTime endDate,
                             BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netProfit,
                             Double incomeVariation, Double expensesVariation, Long orderCount,
                             List<TopDishResponse> top5Dishes) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
        this.incomeVariation = incomeVariation;
        this.expensesVariation = expensesVariation;
        this.orderCount = orderCount;
        this.top5Dishes = top5Dishes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
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

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public List<TopDishResponse> getTop5Dishes() {
        return top5Dishes;
    }

    public void setTop5Dishes(List<TopDishResponse> top5Dishes) {
        this.top5Dishes = top5Dishes;
    }

    public static class Builder {
        private String period;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private Double incomeVariation;
        private Double expensesVariation;
        private Long orderCount;
        private List<TopDishResponse> top5Dishes;

        public Builder period(String period) {
            this.period = period;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

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

        public Builder orderCount(Long orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public Builder top5Dishes(List<TopDishResponse> top5Dishes) {
            this.top5Dishes = top5Dishes;
            return this;
        }

        public DashboardResponse build() {
            return new DashboardResponse(period, startDate, endDate, totalIncome, totalExpenses, netProfit,
                    incomeVariation, expensesVariation, orderCount, top5Dishes);
        }
    }
}

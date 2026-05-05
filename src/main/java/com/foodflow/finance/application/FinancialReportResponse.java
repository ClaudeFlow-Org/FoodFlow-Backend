package com.foodflow.finance.application;

import java.time.LocalDateTime;
import java.util.List;

public class FinancialReportResponse {

    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private FinancialMetricsResponse metrics;
    private List<TopDishResponse> topDishes;
    private List<ExpenseCategoryResponse> expenseBreakdown;
    private Long orderCount;

    public FinancialReportResponse() {
    }

    public FinancialReportResponse(String period, LocalDateTime startDate, LocalDateTime endDate,
                                  FinancialMetricsResponse metrics, List<TopDishResponse> topDishes,
                                  List<ExpenseCategoryResponse> expenseBreakdown, Long orderCount) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metrics = metrics;
        this.topDishes = topDishes;
        this.expenseBreakdown = expenseBreakdown;
        this.orderCount = orderCount;
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

    public FinancialMetricsResponse getMetrics() {
        return metrics;
    }

    public void setMetrics(FinancialMetricsResponse metrics) {
        this.metrics = metrics;
    }

    public List<TopDishResponse> getTopDishes() {
        return topDishes;
    }

    public void setTopDishes(List<TopDishResponse> topDishes) {
        this.topDishes = topDishes;
    }

    public List<ExpenseCategoryResponse> getExpenseBreakdown() {
        return expenseBreakdown;
    }

    public void setExpenseBreakdown(List<ExpenseCategoryResponse> expenseBreakdown) {
        this.expenseBreakdown = expenseBreakdown;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public static class Builder {
        private String period;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private FinancialMetricsResponse metrics;
        private List<TopDishResponse> topDishes;
        private List<ExpenseCategoryResponse> expenseBreakdown;
        private Long orderCount;

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

        public Builder metrics(FinancialMetricsResponse metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder topDishes(List<TopDishResponse> topDishes) {
            this.topDishes = topDishes;
            return this;
        }

        public Builder expenseBreakdown(List<ExpenseCategoryResponse> expenseBreakdown) {
            this.expenseBreakdown = expenseBreakdown;
            return this;
        }

        public Builder orderCount(Long orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public FinancialReportResponse build() {
            return new FinancialReportResponse(period, startDate, endDate, metrics, topDishes, expenseBreakdown, orderCount);
        }
    }
}

package com.foodflow.finance.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportResponse {

    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private FinancialMetricsResponse metrics;
    private List<TopDishResponse> topDishes;
    private List<ExpenseCategoryResponse> expenseBreakdown;
}

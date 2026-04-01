package com.foodflow.finance.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport {

    private ReportPeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private FinancialMetrics metrics;
    private List<TopDish> topDishes;
    private List<ExpenseCategory> expenseBreakdown;
}

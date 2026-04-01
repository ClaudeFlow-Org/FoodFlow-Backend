package com.foodflow.finance.application;

import com.foodflow.finance.domain.*;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderLineItem;
import com.foodflow.sales.domain.OrderRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class FinanceApplicationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getDashboard(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        LocalDateTime yesterdayEnd = todayStart;

        FinancialMetrics todayMetrics = calculateMetrics(userId, todayStart, todayEnd);
        FinancialMetrics yesterdayMetrics = calculateMetrics(userId, yesterdayStart, yesterdayEnd);

        todayMetrics.calculateVariations(yesterdayMetrics);

        List<TopDish> topDishes = getTopDishes(userId, 5);

        return DashboardResponse.builder()
                .totalIncome(todayMetrics.getTotalIncome())
                .totalExpenses(todayMetrics.getTotalExpenses())
                .netProfit(todayMetrics.getNetProfit())
                .incomeVariation(todayMetrics.getIncomeVariation())
                .expensesVariation(todayMetrics.getExpensesVariation())
                .top5Dishes(topDishes.stream().map(this::toTopDishResponse).collect(Collectors.toList()))
                .build();
    }

    public FinancialReportResponse getFinancialReport(Long userId, String periodStr) {
        ReportPeriod period = ReportPeriod.valueOf(periodStr.toUpperCase());
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = period.getPeriodStart(now);
        LocalDateTime end = period.getPeriodEnd(now);

        LocalDateTime previousStart = period.getPreviousPeriodStart(now);
        LocalDateTime previousEnd = period.getPreviousPeriodEnd(now);

        FinancialMetrics currentMetrics = calculateMetrics(userId, start, end);
        FinancialMetrics previousMetrics = calculateMetrics(userId, previousStart, previousEnd);

        currentMetrics.calculateVariations(previousMetrics);

        List<TopDish> topDishes = getTopDishes(userId, 10);
        List<ExpenseCategory> expenseBreakdown = calculateExpenseBreakdown(userId, start, end);

        return FinancialReportResponse.builder()
                .period(period.name())
                .startDate(start)
                .endDate(end)
                .metrics(toMetricsResponse(currentMetrics))
                .topDishes(topDishes.stream().map(this::toTopDishResponse).collect(Collectors.toList()))
                .expenseBreakdown(expenseBreakdown.stream().map(this::toExpenseCategoryResponse).collect(Collectors.toList()))
                .build();
    }

    private FinancialMetrics calculateMetrics(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByUserIdAndDateBetween(userId, start, end);

        BigDecimal totalIncome = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Product> products = productRepository.findByUserId(userId);
        BigDecimal totalExpenses = products.stream()
                .map(p -> p.getUnitCost().multiply(p.getStockLevel()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = totalIncome.subtract(totalExpenses);

        return FinancialMetrics.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .build();
    }

    private List<TopDish> getTopDishes(Long userId, int limit) {
        List<Order> orders = orderRepository.findByUserId(userId);

        Map<Long, TopDish> dishStats = new HashMap<>();

        for (Order order : orders) {
            for (OrderLineItem item : order.getLineItems()) {
                dishStats.computeIfAbsent(item.getDishId(), k -> TopDish.builder()
                        .dishId(item.getDishId())
                        .dishName(item.getDishName())
                        .quantitySold(0)
                        .totalRevenue(BigDecimal.ZERO)
                        .build());

                TopDish topDish = dishStats.get(item.getDishId());
                topDish.setQuantitySold(topDish.getQuantitySold() + item.getQuantity());
                topDish.setTotalRevenue(topDish.getTotalRevenue().add(item.getLineTotal()));
            }
        }

        return dishStats.values().stream()
                .sorted((a, b) -> b.getQuantitySold().compareTo(a.getQuantitySold()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ExpenseCategory> calculateExpenseBreakdown(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Product> products = productRepository.findByUserId(userId);

        Map<String, BigDecimal> categoryExpenses = new LinkedHashMap<>();
        categoryExpenses.put("Inventory", products.stream()
                .map(p -> p.getUnitCost().multiply(p.getStockLevel()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal totalExpenses = categoryExpenses.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryExpenses.entrySet().stream()
                .map(entry -> {
                    ExpenseCategory category = ExpenseCategory.builder()
                            .name(entry.getKey())
                            .amount(entry.getValue())
                            .build();
                    category.calculatePercentage(totalExpenses);
                    return category;
                })
                .collect(Collectors.toList());
    }

    private FinancialMetricsResponse toMetricsResponse(FinancialMetrics metrics) {
        return FinancialMetricsResponse.builder()
                .totalIncome(metrics.getTotalIncome())
                .totalExpenses(metrics.getTotalExpenses())
                .netProfit(metrics.getNetProfit())
                .incomeVariation(metrics.getIncomeVariation())
                .expensesVariation(metrics.getExpensesVariation())
                .build();
    }

    private TopDishResponse toTopDishResponse(TopDish topDish) {
        return TopDishResponse.builder()
                .dishId(topDish.getDishId())
                .dishName(topDish.getDishName())
                .quantitySold(topDish.getQuantitySold())
                .totalRevenue(topDish.getTotalRevenue())
                .build();
    }

    private ExpenseCategoryResponse toExpenseCategoryResponse(ExpenseCategory category) {
        return ExpenseCategoryResponse.builder()
                .name(category.getName())
                .amount(category.getAmount())
                .percentage(category.getPercentage())
                .build();
    }
}

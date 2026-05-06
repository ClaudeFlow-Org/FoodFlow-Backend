package com.foodflow.finance.application;

import com.foodflow.common.domain.ValidationException;
import com.foodflow.finance.domain.*;
import com.foodflow.inventory.domain.InventoryPurchase;
import com.foodflow.inventory.domain.InventoryPurchaseRepository;
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

    private static final String UNCATEGORIZED = "Sin categoria";

    private final OrderRepository orderRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;

    public DashboardResponse getDashboard(Long userId, String periodStr) {
        ReportPeriod period = parsePeriod(periodStr);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = period.getPeriodStart(now);
        LocalDateTime end = period.getPeriodEnd(now);
        LocalDateTime previousStart = period.getPreviousPeriodStart(now);
        LocalDateTime previousEnd = period.getPreviousPeriodEnd(now);

        FinancialMetrics currentMetrics = calculateMetrics(userId, start, end);
        FinancialMetrics previousMetrics = calculateMetrics(userId, previousStart, previousEnd);
        currentMetrics.calculateVariations(previousMetrics);

        List<Order> ordersInPeriod = orderRepository.findByUserIdAndDateBetween(userId, start, end);
        List<TopDish> topDishes = getTopDishesFromOrders(ordersInPeriod, 5);

        return DashboardResponse.builder()
                .period(period.name())
                .startDate(start)
                .endDate(end)
                .totalIncome(currentMetrics.getTotalIncome())
                .totalExpenses(currentMetrics.getTotalExpenses())
                .netProfit(currentMetrics.getNetProfit())
                .incomeVariation(currentMetrics.getIncomeVariation())
                .expensesVariation(currentMetrics.getExpensesVariation())
                .orderCount((long) ordersInPeriod.size())
                .top5Dishes(topDishes.stream().map(this::toTopDishResponse).collect(Collectors.toList()))
                .build();
    }

    public FinancialReportResponse getFinancialReport(Long userId, String periodStr) {
        ReportPeriod period = parsePeriod(periodStr);
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = period.getPeriodStart(now);
        LocalDateTime end = period.getPeriodEnd(now);

        LocalDateTime previousStart = period.getPreviousPeriodStart(now);
        LocalDateTime previousEnd = period.getPreviousPeriodEnd(now);

        FinancialMetrics currentMetrics = calculateMetrics(userId, start, end);
        FinancialMetrics previousMetrics = calculateMetrics(userId, previousStart, previousEnd);

        currentMetrics.calculateVariations(previousMetrics);

        List<Order> ordersInPeriod = orderRepository.findByUserIdAndDateBetween(userId, start, end);
        List<TopDish> topDishes = getTopDishesFromOrders(ordersInPeriod, 10);
        List<ExpenseCategory> expenseBreakdown = calculateExpenseBreakdown(userId, start, end);
        Long orderCount = (long) ordersInPeriod.size();

        return FinancialReportResponse.builder()
                .period(period.name())
                .startDate(start)
                .endDate(end)
                .metrics(toMetricsResponse(currentMetrics))
                .topDishes(topDishes.stream().map(this::toTopDishResponse).collect(Collectors.toList()))
                .expenseBreakdown(expenseBreakdown.stream().map(this::toExpenseCategoryResponse).collect(Collectors.toList()))
                .orderCount(orderCount)
                .build();
    }

    private FinancialMetrics calculateMetrics(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByUserIdAndDateBetween(userId, start, end);

        BigDecimal totalIncome = orders.stream()
                .filter(this::isDelivered)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = calculateExpenses(userId, start, end);
        BigDecimal netProfit = totalIncome.subtract(totalExpenses);

        return FinancialMetrics.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .build();
    }

    private List<TopDish> getTopDishesFromOrders(List<Order> orders, int limit) {
        Map<Long, TopDish> dishStats = new HashMap<>();

        for (Order order : orders.stream().filter(this::isDelivered).toList()) {
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
        Map<String, BigDecimal> categoryExpenses = new LinkedHashMap<>();

        List<InventoryPurchase> purchases = inventoryPurchaseRepository.findByUserIdAndPurchasedAtBetween(userId, start, end);
        for (InventoryPurchase purchase : purchases) {
            categoryExpenses.merge(
                    categoryOrDefault(purchase.getCategory()),
                    safeAmount(purchase.getTotalCost()),
                    BigDecimal::add
            );
        }

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
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateExpenses(Long userId, LocalDateTime start, LocalDateTime end) {
        return inventoryPurchaseRepository.findByUserIdAndPurchasedAtBetween(userId, start, end).stream()
                .map(InventoryPurchase::getTotalCost)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private boolean isDelivered(Order order) {
        return order.getStatus() == Order.OrderStatus.ENTREGADA;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private String categoryOrDefault(String category) {
        if (category == null || category.isBlank()) {
            return UNCATEGORIZED;
        }

        return switch (category.trim().toUpperCase()) {
            case "MEAT" -> "Carnes";
            case "VEGETABLES" -> "Vegetales";
            case "DAIRY" -> "Lacteos";
            case "BEVERAGES" -> "Bebidas";
            case "BAKERY" -> "Panaderia";
            case "CONDIMENTS" -> "Condimentos";
            case "GRAINS" -> "Granos";
            case "SNACKS" -> "Snacks";
            case "OTHER" -> "Otro";
            default -> category.trim();
        };
    }

    private ReportPeriod parsePeriod(String periodStr) {
        try {
            return ReportPeriod.valueOf((periodStr == null ? "DAILY" : periodStr).toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("period", "Period must be DAILY, WEEKLY, or MONTHLY");
        }
    }
}

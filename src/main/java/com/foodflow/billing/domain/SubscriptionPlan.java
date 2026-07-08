package com.foodflow.billing.domain;

import com.foodflow.common.domain.ValidationException;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionPlan {

    FREE("Free", 0.0, 50, 100, 200, Arrays.asList(
            "Basic dashboard access",
            "Up to 50 dishes",
            "Up to 100 products",
            "Up to 200 orders"
    )),
    STANDARD("Standard", 29.99, 200, 500, 1000, Arrays.asList(
            "Full dashboard access",
            "Up to 200 dishes",
            "Up to 500 products",
            "Up to 1000 orders",
            "Weekly reports"
    )),
    PREMIUM("Premium", 79.99, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Arrays.asList(
            "All Standard features",
            "Unlimited dishes",
            "Unlimited products",
            "Unlimited orders",
            "Advanced analytics",
            "Monthly and yearly reports",
            "Expense breakdown by category",
            "Priority support"
    ));

    /** Sentinel value used to represent an unlimited plan allowance. */
    public static final int UNLIMITED = Integer.MAX_VALUE;

    private final String displayName;
    private final Double monthlyPrice;
    private final int maxDishes;
    private final int maxProducts;
    private final int maxOrdersPerMonth;
    private final List<String> benefits;

    SubscriptionPlan(String displayName, Double monthlyPrice, int maxDishes, int maxProducts,
                     int maxOrdersPerMonth, List<String> benefits) {
        this.displayName = displayName;
        this.monthlyPrice = monthlyPrice;
        this.maxDishes = maxDishes;
        this.maxProducts = maxProducts;
        this.maxOrdersPerMonth = maxOrdersPerMonth;
        this.benefits = benefits;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getMonthlyPrice() {
        return monthlyPrice;
    }

    public int getMaxDishes() {
        return maxDishes;
    }

    public int getMaxProducts() {
        return maxProducts;
    }

    public int getMaxOrdersPerMonth() {
        return maxOrdersPerMonth;
    }

    public boolean isUnlimited(int limit) {
        return limit >= UNLIMITED;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public static SubscriptionPlan fromDisplayName(String displayName) {
        for (SubscriptionPlan plan : values()) {
            if (plan.displayName.equalsIgnoreCase(displayName)) {
                return plan;
            }
        }
        throw new ValidationException("plan", "Invalid subscription plan: " + displayName);
    }
}

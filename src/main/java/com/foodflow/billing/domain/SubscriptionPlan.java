package com.foodflow.billing.domain;

import com.foodflow.common.domain.ValidationException;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionPlan {

    FREE("Free", 0.0, Arrays.asList(
            "Basic dashboard access",
            "Up to 50 dishes",
            "Up to 100 products",
            "Up to 200 orders"
    )),
    STANDARD("Standard", 29.99, Arrays.asList(
            "Full dashboard access",
            "Up to 200 dishes",
            "Up to 500 products",
            "Up to 1000 orders",
            "Weekly reports"
    )),
    PREMIUM("Premium", 79.99, Arrays.asList(
            "All Standard features",
            "Unlimited dishes",
            "Unlimited products",
            "Unlimited orders",
            "Advanced analytics",
            "Monthly and yearly reports",
            "Expense breakdown by category",
            "Priority support"
    ));

    private final String displayName;
    private final Double monthlyPrice;
    private final List<String> benefits;

    SubscriptionPlan(String displayName, Double monthlyPrice, List<String> benefits) {
        this.displayName = displayName;
        this.monthlyPrice = monthlyPrice;
        this.benefits = benefits;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getMonthlyPrice() {
        return monthlyPrice;
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

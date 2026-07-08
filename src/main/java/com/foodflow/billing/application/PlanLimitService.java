package com.foodflow.billing.application;

import com.foodflow.billing.domain.Subscription;
import com.foodflow.billing.domain.SubscriptionPlan;
import com.foodflow.billing.domain.SubscriptionRepository;
import com.foodflow.common.domain.PlanLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Enforces the resource allowances advertised by each subscription plan.
 * A user without an active paid subscription is treated as {@link SubscriptionPlan#FREE}.
 */
@Service
@RequiredArgsConstructor
public class PlanLimitService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionPlan activePlan(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .filter(Subscription::isActive)
                .map(Subscription::getPlan)
                .orElse(SubscriptionPlan.FREE);
    }

    public void assertCanAddProduct(Long userId, long currentProductCount) {
        SubscriptionPlan plan = activePlan(userId);
        int limit = plan.getMaxProducts();
        if (currentProductCount >= limit) {
            throw new PlanLimitExceededException("products", plan.getDisplayName(), limit);
        }
    }

    public void assertCanAddDish(Long userId, long currentDishCount) {
        SubscriptionPlan plan = activePlan(userId);
        int limit = plan.getMaxDishes();
        if (currentDishCount >= limit) {
            throw new PlanLimitExceededException("dishes", plan.getDisplayName(), limit);
        }
    }

    public void assertCanCreateOrder(Long userId, long ordersThisMonth) {
        SubscriptionPlan plan = activePlan(userId);
        int limit = plan.getMaxOrdersPerMonth();
        if (ordersThisMonth >= limit) {
            throw new PlanLimitExceededException("orders per month", plan.getDisplayName(), limit);
        }
    }
}

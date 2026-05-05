package com.foodflow.billing.application;

import com.foodflow.billing.domain.Subscription;
import com.foodflow.billing.domain.SubscriptionPlan;
import com.foodflow.billing.domain.SubscriptionRepository;
import com.foodflow.common.domain.NotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BillingApplicationService {

    private final SubscriptionRepository subscriptionRepository;

    public List<SubscriptionPlanResponse> getAvailablePlans() {
        return List.of(SubscriptionPlan.values()).stream()
                .map(this::toPlanResponse)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse subscribe(Long userId, SubscribeRequest request) {
        SubscriptionPlan newPlan = SubscriptionPlan.valueOf(request.getPlan().toUpperCase());

        // Check for existing subscription
        Optional<Subscription> existingSubscriptionOpt = subscriptionRepository.findByUserId(userId);

        Subscription subscription;
        if (existingSubscriptionOpt.isPresent() && existingSubscriptionOpt.get().isActive()) {
            // Upgrade or downgrade existing subscription
            Subscription existing = existingSubscriptionOpt.get();

            // Update the existing subscription with the new plan
            existing.changePlan(newPlan);
            existing.setStartDate(LocalDateTime.now());
            existing.setEndDate(calculateEndDate(newPlan));

            subscription = subscriptionRepository.save(existing);
        } else {
            // Create new subscription
            LocalDateTime endDate = calculateEndDate(newPlan);

            subscription = Subscription.builder()
                    .id(Subscription.SubscriptionId.generate())
                    .userId(userId)
                    .plan(newPlan)
                    .status(Subscription.SubscriptionStatus.ACTIVE)
                    .startDate(LocalDateTime.now())
                    .endDate(endDate)
                    .cancellationDate(null)
                    .stripeSubscriptionId(null)
                    .build();

            subscription = subscriptionRepository.save(subscription);
        }

        return toResponse(subscription);
    }

    public SubscriptionResponse cancelSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Subscription", "user id " + userId));

        subscription.cancel();

        if (subscription.getEndDate() == null || subscription.getEndDate().isBefore(LocalDateTime.now())) {
            subscription.setEndDate(LocalDateTime.now());
        }

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return toResponse(savedSubscription);
    }

    public SubscriptionResponse getCurrentSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Subscription", "user id " + userId));

        return toResponse(subscription);
    }

    private LocalDateTime calculateEndDate(SubscriptionPlan plan) {
        if (plan == SubscriptionPlan.FREE) {
            return null;
        }
        return LocalDateTime.now().plusMonths(1);
    }

    private SubscriptionPlanResponse toPlanResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .name(plan.getDisplayName())
                .monthlyPrice(plan.getMonthlyPrice())
                .benefits(plan.getBenefits())
                .build();
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId().value())
                .plan(subscription.getPlan().getDisplayName())
                .status(subscription.getStatus().name())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .cancellationDate(subscription.getCancellationDate())
                .build();
    }
}

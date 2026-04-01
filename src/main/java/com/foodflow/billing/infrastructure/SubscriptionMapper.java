package com.foodflow.billing.infrastructure;

import com.foodflow.billing.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public Subscription toDomain(SubscriptionJpaEntity entity) {
        return Subscription.builder()
                .id(Subscription.SubscriptionId.of(entity.getId()))
                .userId(entity.getUserId())
                .plan(entity.getPlan().toDomain())
                .status(entity.getStatus().toDomain())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .cancellationDate(entity.getCancellationDate())
                .stripeSubscriptionId(entity.getStripeSubscriptionId())
                .build();
    }

    public SubscriptionJpaEntity toEntity(Subscription domain) {
        return SubscriptionJpaEntity.builder()
                .id(domain.getId() != null ? domain.getId().value() : null)
                .userId(domain.getUserId())
                .plan(SubscriptionPlanEnum.fromDomain(domain.getPlan()))
                .status(SubscriptionStatusEnum.fromDomain(domain.getStatus()))
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .cancellationDate(domain.getCancellationDate())
                .stripeSubscriptionId(domain.getStripeSubscriptionId())
                .build();
    }
}

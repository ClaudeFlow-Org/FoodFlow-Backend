package com.foodflow.billing.infrastructure;

import com.foodflow.billing.domain.Subscription;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum SubscriptionStatusEnum {

    ACTIVE,
    CANCELLED,
    EXPIRED;

    public static SubscriptionStatusEnum fromDomain(Subscription.SubscriptionStatus domain) {
        return switch (domain) {
            case ACTIVE -> ACTIVE;
            case CANCELLED -> CANCELLED;
            case EXPIRED -> EXPIRED;
        };
    }

    public Subscription.SubscriptionStatus toDomain() {
        return switch (this) {
            case ACTIVE -> Subscription.SubscriptionStatus.ACTIVE;
            case CANCELLED -> Subscription.SubscriptionStatus.CANCELLED;
            case EXPIRED -> Subscription.SubscriptionStatus.EXPIRED;
        };
    }

    @Converter(autoApply = true)
    public static class SubscriptionStatusConverter implements AttributeConverter<Subscription.SubscriptionStatus, SubscriptionStatusEnum> {

        @Override
        public SubscriptionStatusEnum convertToDatabaseColumn(Subscription.SubscriptionStatus attribute) {
            return fromDomain(attribute);
        }

        @Override
        public Subscription.SubscriptionStatus convertToEntityAttribute(SubscriptionStatusEnum dbData) {
            return dbData.toDomain();
        }
    }
}

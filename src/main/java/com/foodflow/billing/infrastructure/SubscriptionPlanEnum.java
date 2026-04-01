package com.foodflow.billing.infrastructure;

import com.foodflow.billing.domain.SubscriptionPlan;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionPlanEnum {

    FREE,
    STANDARD,
    PREMIUM;

    public static SubscriptionPlanEnum fromDomain(SubscriptionPlan domain) {
        return switch (domain) {
            case FREE -> FREE;
            case STANDARD -> STANDARD;
            case PREMIUM -> PREMIUM;
        };
    }

    public SubscriptionPlan toDomain() {
        return switch (this) {
            case FREE -> SubscriptionPlan.FREE;
            case STANDARD -> SubscriptionPlan.STANDARD;
            case PREMIUM -> SubscriptionPlan.PREMIUM;
        };
    }

    @Converter(autoApply = true)
    public static class SubscriptionPlanConverter implements AttributeConverter<SubscriptionPlan, SubscriptionPlanEnum> {

        @Override
        public SubscriptionPlanEnum convertToDatabaseColumn(SubscriptionPlan attribute) {
            return fromDomain(attribute);
        }

        @Override
        public SubscriptionPlan convertToEntityAttribute(SubscriptionPlanEnum dbData) {
            return dbData.toDomain();
        }
    }
}

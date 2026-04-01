package com.foodflow.billing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    private SubscriptionId id;
    private Long userId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancellationDate;
    private String stripeSubscriptionId;

    public enum SubscriptionStatus {
        ACTIVE,
        CANCELLED,
        EXPIRED
    }

    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new ValidationException("Subscription is already cancelled");
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE &&
                (this.endDate == null || this.endDate.isAfter(LocalDateTime.now()));
    }

    public record SubscriptionId(String value) {
        public static SubscriptionId of(String value) {
            return new SubscriptionId(value);
        }

        public static SubscriptionId generate() {
            return new SubscriptionId(UUID.randomUUID().toString());
        }

        public static SubscriptionId empty() {
            return new SubscriptionId(null);
        }
    }
}

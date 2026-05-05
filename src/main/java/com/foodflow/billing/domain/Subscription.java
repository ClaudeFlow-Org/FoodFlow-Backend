package com.foodflow.billing.domain;

import com.foodflow.common.domain.ValidationException;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // Constructors
    public Subscription() {
    }

    public Subscription(SubscriptionId id, Long userId, SubscriptionPlan plan, SubscriptionStatus status,
                        LocalDateTime startDate, LocalDateTime endDate, LocalDateTime cancellationDate, String stripeSubscriptionId) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancellationDate = cancellationDate;
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public SubscriptionId getId() {
        return id;
    }

    public void setId(SubscriptionId id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    // Business methods
    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new ValidationException("Subscription is already cancelled");
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationDate = LocalDateTime.now();
    }

    public void changePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan;
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

    public static class Builder {
        private SubscriptionId id;
        private Long userId;
        private SubscriptionPlan plan;
        private SubscriptionStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private LocalDateTime cancellationDate;
        private String stripeSubscriptionId;

        public Builder id(SubscriptionId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder plan(SubscriptionPlan plan) {
            this.plan = plan;
            return this;
        }

        public Builder status(SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder cancellationDate(LocalDateTime cancellationDate) {
            this.cancellationDate = cancellationDate;
            return this;
        }

        public Builder stripeSubscriptionId(String stripeSubscriptionId) {
            this.stripeSubscriptionId = stripeSubscriptionId;
            return this;
        }

        public Subscription build() {
            return new Subscription(id, userId, plan, status, startDate, endDate, cancellationDate, stripeSubscriptionId);
        }
    }
}

package com.foodflow.billing.infrastructure;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class SubscriptionJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private SubscriptionPlanEnum plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatusEnum status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    public SubscriptionJpaEntity() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SubscriptionPlanEnum getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlanEnum plan) {
        this.plan = plan;
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
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

    public static class Builder {
        private String id;
        private Long userId;
        private SubscriptionPlanEnum plan;
        private SubscriptionStatusEnum status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private LocalDateTime cancellationDate;
        private String stripeSubscriptionId;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder plan(SubscriptionPlanEnum plan) {
            this.plan = plan;
            return this;
        }

        public Builder status(SubscriptionStatusEnum status) {
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

        public SubscriptionJpaEntity build() {
            SubscriptionJpaEntity entity = new SubscriptionJpaEntity();
            entity.id = this.id;
            entity.userId = this.userId;
            entity.plan = this.plan;
            entity.status = this.status;
            entity.startDate = this.startDate;
            entity.endDate = this.endDate;
            entity.cancellationDate = this.cancellationDate;
            entity.stripeSubscriptionId = this.stripeSubscriptionId;
            return entity;
        }
    }
}

package com.foodflow.billing.application;

import java.time.LocalDateTime;

public class SubscriptionResponse {

    private String id;
    private String plan;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancellationDate;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(String id, String plan, String status, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime cancellationDate) {
        this.id = id;
        this.plan = plan;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancellationDate = cancellationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String plan;
        private String status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private LocalDateTime cancellationDate;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder plan(String plan) {
            this.plan = plan;
            return this;
        }

        public Builder status(String status) {
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

        public SubscriptionResponse build() {
            return new SubscriptionResponse(id, plan, status, startDate, endDate, cancellationDate);
        }
    }
}

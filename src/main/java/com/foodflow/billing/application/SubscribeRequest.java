package com.foodflow.billing.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SubscribeRequest {

    @NotBlank(message = "Plan is required")
    @Pattern(regexp = "FREE|STANDARD|PREMIUM", message = "Plan must be FREE, STANDARD, or PREMIUM")
    private String plan;

    public SubscribeRequest() {
    }

    public SubscribeRequest(String plan) {
        this.plan = plan;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String plan;

        public Builder plan(String plan) {
            this.plan = plan;
            return this;
        }

        public SubscribeRequest build() {
            return new SubscribeRequest(plan);
        }
    }
}

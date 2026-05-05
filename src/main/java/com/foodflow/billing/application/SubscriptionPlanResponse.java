package com.foodflow.billing.application;

import java.util.List;

public class SubscriptionPlanResponse {

    private String name;
    private Double monthlyPrice;
    private List<String> benefits;

    public SubscriptionPlanResponse() {
    }

    public SubscriptionPlanResponse(String name, Double monthlyPrice, List<String> benefits) {
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.benefits = benefits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(Double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Double monthlyPrice;
        private List<String> benefits;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder monthlyPrice(Double monthlyPrice) {
            this.monthlyPrice = monthlyPrice;
            return this;
        }

        public Builder benefits(List<String> benefits) {
            this.benefits = benefits;
            return this;
        }

        public SubscriptionPlanResponse build() {
            return new SubscriptionPlanResponse(name, monthlyPrice, benefits);
        }
    }
}

package com.foodflow.finance.application;

import java.math.BigDecimal;

public class ExpenseCategoryResponse {

    private String name;
    private BigDecimal amount;
    private BigDecimal percentage;

    public ExpenseCategoryResponse() {
    }

    public ExpenseCategoryResponse(String name, BigDecimal amount, BigDecimal percentage) {
        this.name = name;
        this.amount = amount;
        this.percentage = percentage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public static class Builder {
        private String name;
        private BigDecimal amount;
        private BigDecimal percentage;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder percentage(BigDecimal percentage) {
            this.percentage = percentage;
            return this;
        }

        public ExpenseCategoryResponse build() {
            return new ExpenseCategoryResponse(name, amount, percentage);
        }
    }
}

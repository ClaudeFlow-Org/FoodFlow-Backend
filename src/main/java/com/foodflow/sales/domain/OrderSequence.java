package com.foodflow.sales.domain;

/**
 * Entity to manage order number sequences per user.
 * Each user has their own sequence that starts at 1 and increments with each order.
 */
public class OrderSequence {

    private Long userId;
    private Long nextValue;

    public OrderSequence() {
    }

    public OrderSequence(Long userId, Long nextValue) {
        this.userId = userId;
        this.nextValue = nextValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNextValue() {
        return nextValue;
    }

    public void setNextValue(Long nextValue) {
        this.nextValue = nextValue;
    }

    /**
     * Get the next sequence number and increment the counter
     */
    public Long getNextAndIncrement() {
        Long current = this.nextValue;
        this.nextValue = current + 1;
        return current;
    }

    /**
     * Get the current value without incrementing
     */
    public Long getCurrentValue() {
        return this.nextValue;
    }

    /**
     * Reset the sequence to a specific value
     */
    public void resetTo(Long newValue) {
        if (newValue < 1) {
            this.nextValue = 1L;
        } else {
            this.nextValue = newValue;
        }
    }

    public static class Builder {
        private Long userId;
        private Long nextValue;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder nextValue(Long nextValue) {
            this.nextValue = nextValue;
            return this;
        }

        public OrderSequence build() {
            return new OrderSequence(userId, nextValue);
        }
    }
}

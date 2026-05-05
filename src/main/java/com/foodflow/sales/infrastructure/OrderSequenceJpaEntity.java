package com.foodflow.sales.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "order_sequences")
public class OrderSequenceJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;

    public OrderSequenceJpaEntity() {
    }

    public OrderSequenceJpaEntity(Long userId, Long nextValue) {
        this.userId = userId;
        this.nextValue = nextValue;
    }

    public static Builder builder() {
        return new Builder();
    }

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

        public OrderSequenceJpaEntity build() {
            return new OrderSequenceJpaEntity(userId, nextValue);
        }
    }
}

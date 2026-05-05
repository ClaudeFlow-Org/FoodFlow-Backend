package com.foodflow.sales.domain;

import java.util.Optional;

public interface OrderSequenceRepository {

    Optional<OrderSequence> findByUserId(Long userId);

    OrderSequence save(OrderSequence sequence);

    void deleteByUserId(Long userId);
}

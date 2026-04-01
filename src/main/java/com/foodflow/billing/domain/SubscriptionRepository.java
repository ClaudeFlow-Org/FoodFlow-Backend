package com.foodflow.billing.domain;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Subscription.SubscriptionId id);

    Optional<Subscription> findByUserId(Long userId);

    List<Subscription> findAllActive();
}

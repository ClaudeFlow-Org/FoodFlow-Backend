package com.foodflow.billing.infrastructure;

import com.foodflow.billing.domain.Subscription;
import com.foodflow.billing.domain.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionJpaEntity entity = subscriptionMapper.toEntity(subscription);
        SubscriptionJpaEntity savedEntity = jpaRepository.save(entity);
        return subscriptionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Subscription> findById(Subscription.SubscriptionId id) {
        return jpaRepository.findById(id.value())
                .map(subscriptionMapper::toDomain);
    }

    @Override
    public Optional<Subscription> findByUserId(Long userId) {
        return jpaRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream()
                .findFirst()
                .map(subscriptionMapper::toDomain);
    }

    @Override
    public List<Subscription> findAllActive() {
        return jpaRepository.findByStatus(SubscriptionStatusEnum.ACTIVE).stream()
                .map(subscriptionMapper::toDomain)
                .toList();
    }
}

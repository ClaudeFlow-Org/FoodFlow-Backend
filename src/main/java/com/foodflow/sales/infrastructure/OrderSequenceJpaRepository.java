package com.foodflow.sales.infrastructure;

import com.foodflow.sales.domain.OrderSequence;
import com.foodflow.sales.domain.OrderSequenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderSequenceJpaRepository implements OrderSequenceRepository {

    private final OrderSequenceJpa jpaRepository;

    public OrderSequenceJpaRepository(OrderSequenceJpa jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<OrderSequence> findByUserId(Long userId) {
        return jpaRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public OrderSequence save(OrderSequence sequence) {
        OrderSequenceJpaEntity entity = toEntity(sequence);
        OrderSequenceJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteById(userId);
    }

    private OrderSequence toDomain(OrderSequenceJpaEntity entity) {
        return OrderSequence.builder()
                .userId(entity.getUserId())
                .nextValue(entity.getNextValue())
                .build();
    }

    private OrderSequenceJpaEntity toEntity(OrderSequence domain) {
        return OrderSequenceJpaEntity.builder()
                .userId(domain.getUserId())
                .nextValue(domain.getNextValue())
                .build();
    }
}

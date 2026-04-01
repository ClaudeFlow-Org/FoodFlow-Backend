package com.foodflow.sales.infrastructure;

import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderMapper.toEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Order.OrderId id) {
        return jpaRepository.findById(id.value())
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jpaRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByUserIdAndDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByUserIdAndDateBetween(userId, startDate, endDate).stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Order.OrderId id) {
        jpaRepository.deleteById(id.value());
    }
}

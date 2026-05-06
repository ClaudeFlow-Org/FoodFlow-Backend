package com.foodflow.sales.infrastructure;

import com.foodflow.sales.domain.Order;
import com.foodflow.sales.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderRepositoryImpl implements OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Order save(Order order) {
        OrderJpaEntity entity = orderMapper.toEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Order.OrderId id) {
        try {
            return jpaRepository.findByIdWithLineItems(id.value())
                    .map(orderMapper::toDomain);
        } catch (RuntimeException ex) {
            log.warn("Falling back to basic order lookup for id {}: {}", id.value(), ex.getMessage());
            return jpaRepository.findById(id.value())
                    .map(orderMapper::toDomain);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        try {
            return mapOrders(jpaRepository.findByUserIdWithLineItemsOrderByOrderDateDesc(userId));
        } catch (RuntimeException ex) {
            log.warn("Falling back to basic order list for user {}: {}", userId, ex.getMessage());
            return mapOrders(jpaRepository.findByUserIdOrderByOrderDateDesc(userId));
        }
    }

    @Override
    public List<Order> findByUserIdAndDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return mapOrders(jpaRepository.findByUserIdAndDateBetweenWithLineItems(userId, startDate, endDate));
        } catch (RuntimeException ex) {
            log.warn("Falling back to basic dated order list for user {}: {}", userId, ex.getMessage());
            return mapOrders(jpaRepository.findByUserIdAndOrderDateGreaterThanEqualAndOrderDateBefore(userId, startDate, endDate));
        }
    }

    @Override
    @Transactional
    public void delete(Order.OrderId id) {
        jpaRepository.deleteById(id.value());
    }

    private List<Order> mapOrders(List<OrderJpaEntity> entities) {
        return entities.stream()
                .map(orderMapper::toDomain)
                .toList();
    }
}

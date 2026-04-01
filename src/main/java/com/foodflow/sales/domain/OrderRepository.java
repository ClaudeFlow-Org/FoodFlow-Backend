package com.foodflow.sales.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Order.OrderId id);

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    void delete(Order.OrderId id);
}

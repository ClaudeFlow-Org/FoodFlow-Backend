package com.foodflow.sales.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSequenceJpa extends JpaRepository<OrderSequenceJpaEntity, Long> {
    // CRUD methods are inherited from JpaRepository
}

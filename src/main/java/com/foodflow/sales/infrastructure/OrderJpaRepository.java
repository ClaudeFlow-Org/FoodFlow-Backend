package com.foodflow.sales.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByUserIdOrderByOrderDateDesc(Long userId);

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.userId = :userId AND o.orderDate BETWEEN :startDate AND :endDate")
    List<OrderJpaEntity> findByUserIdAndDateBetween(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
}

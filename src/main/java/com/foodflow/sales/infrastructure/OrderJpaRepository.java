package com.foodflow.sales.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("SELECT DISTINCT o FROM OrderJpaEntity o LEFT JOIN FETCH o.lineItems WHERE o.userId = :userId ORDER BY o.orderDate DESC")
    List<OrderJpaEntity> findByUserIdWithLineItemsOrderByOrderDateDesc(Long userId);

    List<OrderJpaEntity> findByUserIdOrderByOrderDateDesc(Long userId);

    @Query("SELECT DISTINCT o FROM OrderJpaEntity o LEFT JOIN FETCH o.lineItems WHERE o.userId = :userId AND o.orderDate >= :startDate AND o.orderDate < :endDate")
    List<OrderJpaEntity> findByUserIdAndDateBetweenWithLineItems(@Param("userId") Long userId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);

    List<OrderJpaEntity> findByUserIdAndOrderDateGreaterThanEqualAndOrderDateBefore(Long userId,
                                                                                    LocalDateTime startDate,
                                                                                    LocalDateTime endDate);

    @Query("SELECT DISTINCT o FROM OrderJpaEntity o LEFT JOIN FETCH o.lineItems WHERE o.id = :id")
    Optional<OrderJpaEntity> findByIdWithLineItems(@Param("id") Long id);
}

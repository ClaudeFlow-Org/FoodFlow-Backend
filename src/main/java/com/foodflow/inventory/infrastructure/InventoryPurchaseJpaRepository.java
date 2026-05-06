package com.foodflow.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface InventoryPurchaseJpaRepository extends JpaRepository<InventoryPurchaseJpaEntity, Long> {

    @Query("SELECT p FROM InventoryPurchaseJpaEntity p WHERE p.userId = :userId AND p.purchasedAt >= :startDate AND p.purchasedAt < :endDate")
    List<InventoryPurchaseJpaEntity> findByUserIdAndPurchasedAtBetween(@Param("userId") Long userId,
                                                                        @Param("startDate") LocalDateTime startDate,
                                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT p.productId FROM InventoryPurchaseJpaEntity p WHERE p.userId = :userId AND p.productId IS NOT NULL")
    Set<Long> findProductIdsWithPurchases(@Param("userId") Long userId);
}

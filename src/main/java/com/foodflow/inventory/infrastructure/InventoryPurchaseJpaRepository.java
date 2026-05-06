package com.foodflow.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryPurchaseJpaRepository extends JpaRepository<InventoryPurchaseJpaEntity, Long> {

    @Query("SELECT p FROM InventoryPurchaseJpaEntity p WHERE p.userId = :userId AND p.purchasedAt >= :startDate AND p.purchasedAt < :endDate")
    List<InventoryPurchaseJpaEntity> findByUserIdAndPurchasedAtBetween(@Param("userId") Long userId,
                                                                        @Param("startDate") LocalDateTime startDate,
                                                                        @Param("endDate") LocalDateTime endDate);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryPurchaseJpaEntity p SET p.category = :categoryName WHERE p.userId = :userId AND p.productId = :productId")
    void updateProductCategory(@Param("userId") Long userId,
                               @Param("productId") Long productId,
                               @Param("categoryName") String categoryName);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryPurchaseJpaEntity p SET p.category = :newName WHERE p.userId = :userId AND LOWER(p.category) = LOWER(:previousName)")
    void renameCategory(@Param("userId") Long userId,
                        @Param("previousName") String previousName,
                        @Param("newName") String newName);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryPurchaseJpaEntity p SET p.category = NULL WHERE p.userId = :userId AND LOWER(p.category) = LOWER(:categoryName)")
    void clearCategory(@Param("userId") Long userId,
                       @Param("categoryName") String categoryName);
}

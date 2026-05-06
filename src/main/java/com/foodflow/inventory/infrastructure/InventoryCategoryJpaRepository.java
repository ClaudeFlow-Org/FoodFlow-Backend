package com.foodflow.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCategoryJpaRepository extends JpaRepository<InventoryCategoryJpaEntity, Long> {

    List<InventoryCategoryJpaEntity> findByUserIdOrderByNameAsc(Long userId);

    Optional<InventoryCategoryJpaEntity> findByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}

package com.foodflow.inventory.domain;

import java.util.List;
import java.util.Optional;

public interface InventoryCategoryRepository {

    InventoryCategory save(InventoryCategory category);

    Optional<InventoryCategory> findById(InventoryCategory.InventoryCategoryId id);

    List<InventoryCategory> findByUserId(Long userId);

    Optional<InventoryCategory> findByUserIdAndName(Long userId, String name);

    boolean existsByUserIdAndName(Long userId, String name);

    void delete(InventoryCategory.InventoryCategoryId id);
}

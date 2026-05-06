package com.foodflow.inventory.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryPurchaseRepository {

    InventoryPurchase save(InventoryPurchase purchase);

    List<InventoryPurchase> findByUserIdAndPurchasedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void renameCategory(Long userId, String previousName, String newName);

    void clearCategory(Long userId, String categoryName);
}

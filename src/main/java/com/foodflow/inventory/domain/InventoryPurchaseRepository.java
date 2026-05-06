package com.foodflow.inventory.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface InventoryPurchaseRepository {

    InventoryPurchase save(InventoryPurchase purchase);

    List<InventoryPurchase> findByUserIdAndPurchasedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    Set<Long> findProductIdsWithPurchases(Long userId);
}

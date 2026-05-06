package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.InventoryPurchase;
import org.springframework.stereotype.Component;

@Component
public class InventoryPurchaseMapper {

    public InventoryPurchase toDomain(InventoryPurchaseJpaEntity entity) {
        return InventoryPurchase.builder()
                .id(InventoryPurchase.InventoryPurchaseId.of(entity.getId()))
                .userId(entity.getUserId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .category(entity.getCategory())
                .quantity(entity.getQuantity())
                .unitCost(entity.getUnitCost())
                .totalCost(entity.getTotalCost())
                .purchasedAt(entity.getPurchasedAt())
                .build();
    }

    public InventoryPurchaseJpaEntity toEntity(InventoryPurchase domain) {
        var builder = InventoryPurchaseJpaEntity.builder()
                .userId(domain.getUserId())
                .productId(domain.getProductId())
                .productName(domain.getProductName())
                .category(domain.getCategory())
                .quantity(domain.getQuantity())
                .unitCost(domain.getUnitCost())
                .totalCost(domain.getTotalCost())
                .purchasedAt(domain.getPurchasedAt());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}

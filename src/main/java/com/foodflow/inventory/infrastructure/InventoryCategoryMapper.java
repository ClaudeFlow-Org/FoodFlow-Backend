package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.InventoryCategory;
import org.springframework.stereotype.Component;

@Component
public class InventoryCategoryMapper {

    public InventoryCategory toDomain(InventoryCategoryJpaEntity entity) {
        return InventoryCategory.builder()
                .id(InventoryCategory.InventoryCategoryId.of(entity.getId()))
                .userId(entity.getUserId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public InventoryCategoryJpaEntity toEntity(InventoryCategory domain) {
        var builder = InventoryCategoryJpaEntity.builder()
                .userId(domain.getUserId())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}

package com.foodflow.catalog.infrastructure;

import com.foodflow.catalog.domain.DishRecipeItem;
import org.springframework.stereotype.Component;

@Component
public class DishRecipeItemMapper {

    public DishRecipeItem toDomain(DishRecipeItemJpaEntity entity) {
        return DishRecipeItem.builder()
                .id(DishRecipeItem.DishRecipeItemId.of(entity.getId()))
                .userId(entity.getUserId())
                .dishId(entity.getDishId())
                .productId(entity.getProductId())
                .requiredQuantity(entity.getRequiredQuantity())
                .requiredUnitOfMeasure(entity.getRequiredUnitOfMeasure())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DishRecipeItemJpaEntity toEntity(DishRecipeItem domain) {
        var builder = DishRecipeItemJpaEntity.builder()
                .userId(domain.getUserId())
                .dishId(domain.getDishId())
                .productId(domain.getProductId())
                .requiredQuantity(domain.getRequiredQuantity())
                .requiredUnitOfMeasure(domain.getRequiredUnitOfMeasure())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt());

        if (domain.getId() != null && domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}

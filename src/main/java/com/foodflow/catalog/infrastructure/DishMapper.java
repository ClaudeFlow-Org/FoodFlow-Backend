package com.foodflow.catalog.infrastructure;

import com.foodflow.catalog.domain.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishMapper {

    public Dish toDomain(DishJpaEntity entity) {
        return Dish.builder()
                .id(Dish.DishId.of(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .ingredients(entity.getIngredients())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DishJpaEntity toEntity(Dish domain) {
        var builder = DishJpaEntity.builder()
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .ingredients(domain.getIngredients())
                .userId(domain.getUserId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}

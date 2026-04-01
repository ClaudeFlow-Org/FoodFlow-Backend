package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity entity) {
        return Product.builder()
                .id(Product.ProductId.of(entity.getId()))
                .name(entity.getName())
                .stockLevel(entity.getStockLevel())
                .unitCost(entity.getUnitCost())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductJpaEntity toEntity(Product domain) {
        var builder = ProductJpaEntity.builder()
                .name(domain.getName())
                .stockLevel(domain.getStockLevel())
                .unitCost(domain.getUnitCost())
                .unitOfMeasure(domain.getUnitOfMeasure())
                .userId(domain.getUserId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}

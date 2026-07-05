package com.foodflow.catalog.infrastructure;

import com.foodflow.catalog.domain.DishRecipeItem;
import com.foodflow.catalog.domain.DishRecipeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishRecipeItemRepositoryImpl implements DishRecipeItemRepository {

    private final DishRecipeItemJpaRepository jpaRepository;
    private final DishRecipeItemMapper mapper;

    @Override
    public List<DishRecipeItem> findByUserIdAndDishId(Long userId, Long dishId) {
        return jpaRepository.findByUserIdAndDishId(userId, dishId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<DishRecipeItem> findByUserIdAndDishIdIn(Long userId, List<Long> dishIds) {
        if (dishIds == null || dishIds.isEmpty()) {
            return List.of();
        }

        return jpaRepository.findByUserIdAndDishIdIn(userId, dishIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void replaceForDish(Long userId, Long dishId, List<DishRecipeItem> recipeItems) {
        jpaRepository.deleteByUserIdAndDishId(userId, dishId);
        jpaRepository.flush();
        if (recipeItems == null || recipeItems.isEmpty()) {
            return;
        }

        jpaRepository.saveAll(recipeItems.stream()
                .map(mapper::toEntity)
                .toList());
    }

    @Override
    @Transactional
    public void deleteByUserIdAndDishId(Long userId, Long dishId) {
        jpaRepository.deleteByUserIdAndDishId(userId, dishId);
    }
}

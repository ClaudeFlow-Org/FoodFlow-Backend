package com.foodflow.catalog.domain;

import java.util.List;

public interface DishRecipeItemRepository {

    List<DishRecipeItem> findByUserIdAndDishId(Long userId, Long dishId);

    List<DishRecipeItem> findByUserIdAndDishIdIn(Long userId, List<Long> dishIds);

    void replaceForDish(Long userId, Long dishId, List<DishRecipeItem> recipeItems);

    void deleteByUserIdAndDishId(Long userId, Long dishId);
}

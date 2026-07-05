package com.foodflow.catalog.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRecipeItemJpaRepository extends JpaRepository<DishRecipeItemJpaEntity, Long> {

    List<DishRecipeItemJpaEntity> findByUserIdAndDishId(Long userId, Long dishId);

    List<DishRecipeItemJpaEntity> findByUserIdAndDishIdIn(Long userId, List<Long> dishIds);

    void deleteByUserIdAndDishId(Long userId, Long dishId);
}

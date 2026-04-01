package com.foodflow.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface DishRepository {

    Dish save(Dish dish);

    Optional<Dish> findById(Dish.DishId id);

    List<Dish> findByUserId(Long userId);

    List<Dish> findByUserIdAndNameContaining(Long userId, String name);

    void delete(Dish.DishId id);

    boolean existsByUserIdAndName(Long userId, String name);
}

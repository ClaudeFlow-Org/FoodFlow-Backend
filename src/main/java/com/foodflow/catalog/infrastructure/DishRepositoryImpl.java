package com.foodflow.catalog.infrastructure;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class DishRepositoryImpl implements DishRepository {

    private final DishJpaRepository jpaRepository;
    private final DishMapper dishMapper;

    @Override
    public Dish save(Dish dish) {
        DishJpaEntity entity = dishMapper.toEntity(dish);
        DishJpaEntity savedEntity = jpaRepository.save(entity);
        return dishMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dish> findById(Dish.DishId id) {
        return jpaRepository.findById(id.value())
                .map(dishMapper::toDomain);
    }

    @Override
    public List<Dish> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(dishMapper::toDomain)
                .toList();
    }

    @Override
    public List<Dish> findByUserIdAndNameContaining(Long userId, String name) {
        return jpaRepository.findByUserIdAndNameContaining(userId, name).stream()
                .map(dishMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Dish.DishId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return jpaRepository.existsByUserIdAndName(userId, name);
    }
}

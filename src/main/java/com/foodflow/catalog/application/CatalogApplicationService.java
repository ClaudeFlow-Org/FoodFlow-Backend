package com.foodflow.catalog.application;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CatalogApplicationService {

    private final DishRepository dishRepository;

    public DishResponse addDish(Long userId, DishRequest request) {
        if (dishRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new DuplicateResourceException("Dish", "name " + request.getName());
        }

        validatePrice(request.getPrice());

        Dish dish = Dish.builder()
                .id(Dish.DishId.empty())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .ingredients(request.getIngredients())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Dish savedDish = dishRepository.save(dish);

        return toResponse(savedDish);
    }

    public List<DishResponse> getAllDishes(Long userId) {
        return dishRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DishResponse> searchDishes(Long userId, String name) {
        if (name == null || name.isBlank()) {
            return getAllDishes(userId);
        }

        return dishRepository.findByUserIdAndNameContaining(userId, name).stream()
                .map(this::toResponse)
                .toList();
    }

    public DishResponse getDishById(Long userId, Long dishId) {
        Dish dish = dishRepository.findById(Dish.DishId.of(dishId))
                .orElseThrow(() -> new NotFoundException("Dish", "id " + dishId));

        if (!dish.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this dish");
        }

        return toResponse(dish);
    }

    public DishResponse updateDish(Long userId, Long dishId, DishRequest request) {
        Dish dish = dishRepository.findById(Dish.DishId.of(dishId))
                .orElseThrow(() -> new NotFoundException("Dish", "id " + dishId));

        if (!dish.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this dish");
        }

        if (request.getName() != null && !request.getName().equals(dish.getName())) {
            if (dishRepository.existsByUserIdAndName(userId, request.getName())) {
                throw new DuplicateResourceException("Dish", "name " + request.getName());
            }
        }

        validatePrice(request.getPrice());

        dish.updateDetails(request.getName(), request.getDescription(),
                request.getPrice(), request.getIngredients());

        Dish updatedDish = dishRepository.save(dish);

        return toResponse(updatedDish);
    }

    public void deleteDish(Long userId, Long dishId) {
        Dish dish = dishRepository.findById(Dish.DishId.of(dishId))
                .orElseThrow(() -> new NotFoundException("Dish", "id " + dishId));

        if (!dish.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this dish");
        }

        dishRepository.delete(Dish.DishId.of(dishId));
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("price", "Price must be greater than 0");
        }
    }

    private DishResponse toResponse(Dish dish) {
        return DishResponse.builder()
                .id(dish.getId().value())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .ingredients(dish.getIngredients())
                .createdAt(dish.getCreatedAt())
                .build();
    }
}

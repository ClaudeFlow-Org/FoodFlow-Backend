package com.foodflow.catalog.application;

import com.foodflow.catalog.domain.Dish;
import com.foodflow.catalog.domain.DishRepository;
import com.foodflow.catalog.domain.DishRecipeItem;
import com.foodflow.catalog.domain.DishRecipeItemRepository;
import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.MeasurementUnitConverter;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class CatalogApplicationService {

    private final DishRepository dishRepository;
    private final DishRecipeItemRepository dishRecipeItemRepository;
    private final ProductRepository productRepository;

    public CatalogApplicationService(DishRepository dishRepository,
                                     DishRecipeItemRepository dishRecipeItemRepository,
                                     ProductRepository productRepository) {
        this.dishRepository = dishRepository;
        this.dishRecipeItemRepository = dishRecipeItemRepository;
        this.productRepository = productRepository;
    }

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
        replaceRecipeItems(userId, savedDish.getId().value(), request.getRecipeItems());

        return toResponse(savedDish);
    }

    public List<DishResponse> getAllDishes(Long userId) {
        List<Dish> dishes = dishRepository.findByUserId(userId);
        List<Long> dishIds = dishes.stream()
                .map(dish -> dish.getId().value())
                .toList();
        Map<Long, List<DishRecipeItem>> recipeItemsByDish = dishRecipeItemRepository.findByUserIdAndDishIdIn(userId, dishIds).stream()
                .collect(Collectors.groupingBy(DishRecipeItem::getDishId));
        Map<Long, Product> productsById = getProductsById(userId);

        return dishes.stream()
                .map(dish -> toResponse(dish, recipeItemsByDish.getOrDefault(dish.getId().value(), List.of()), productsById))
                .toList();
    }

    public List<DishResponse> searchDishes(Long userId, String name) {
        if (name == null || name.isBlank()) {
            return getAllDishes(userId);
        }

        List<Dish> dishes = dishRepository.findByUserIdAndNameContaining(userId, name);
        Map<Long, Product> productsById = getProductsById(userId);
        return dishes.stream()
                .map(dish -> toResponse(dish, dishRecipeItemRepository.findByUserIdAndDishId(userId, dish.getId().value()), productsById))
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
        if (request.getRecipeItems() != null) {
            replaceRecipeItems(userId, dishId, request.getRecipeItems());
        }

        return toResponse(updatedDish);
    }

    public void deleteDish(Long userId, Long dishId) {
        Dish dish = dishRepository.findById(Dish.DishId.of(dishId))
                .orElseThrow(() -> new NotFoundException("Dish", "id " + dishId));

        if (!dish.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this dish");
        }

        dishRecipeItemRepository.deleteByUserIdAndDishId(userId, dishId);
        dishRepository.delete(Dish.DishId.of(dishId));
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("price", "Price must be greater than 0");
        }
    }

    private void replaceRecipeItems(Long userId, Long dishId, List<DishRecipeItemRequest> recipeRequests) {
        List<DishRecipeItem> recipeItems = buildRecipeItems(userId, dishId, recipeRequests);
        dishRecipeItemRepository.replaceForDish(userId, dishId, recipeItems);
    }

    private List<DishRecipeItem> buildRecipeItems(Long userId, Long dishId, List<DishRecipeItemRequest> recipeRequests) {
        if (recipeRequests == null || recipeRequests.isEmpty()) {
            return List.of();
        }

        Map<Long, Product> productsById = getProductsById(userId);
        Set<Long> productIds = new HashSet<>();
        List<DishRecipeItem> recipeItems = new ArrayList<>();

        for (DishRecipeItemRequest request : recipeRequests) {
            if (request.getProductId() == null) {
                throw new ValidationException("recipeItems.productId", "Product is required");
            }
            if (request.getRequiredQuantity() == null || request.getRequiredQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("recipeItems.requiredQuantity", "Required quantity must be greater than 0");
            }
            if (!productIds.add(request.getProductId())) {
                throw new ValidationException("recipeItems.productId", "Recipe cannot contain the same product twice");
            }
            Product product = productsById.get(request.getProductId());
            if (product == null) {
                throw new ValidationException("recipeItems.productId", "Product does not belong to this user");
            }
            String requiredUnit = resolveRecipeUnit(request.getRequiredUnitOfMeasure(), product, request.getRequiredQuantity());
            validateCompatibleRecipeUnit(requiredUnit, product.getUnitOfMeasure());

            recipeItems.add(DishRecipeItem.builder()
                    .id(DishRecipeItem.DishRecipeItemId.empty())
                    .userId(userId)
                    .dishId(dishId)
                    .productId(request.getProductId())
                    .requiredQuantity(request.getRequiredQuantity())
                    .requiredUnitOfMeasure(requiredUnit)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }

        return recipeItems;
    }

    private Map<Long, Product> getProductsById(Long userId) {
        return productRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(product -> product.getId().value(), product -> product));
    }

    private DishResponse toResponse(Dish dish) {
        return toResponse(
                dish,
                dishRecipeItemRepository.findByUserIdAndDishId(dish.getUserId(), dish.getId().value()),
                getProductsById(dish.getUserId())
        );
    }

    private DishResponse toResponse(Dish dish, List<DishRecipeItem> recipeItems, Map<Long, Product> productsById) {
        List<DishRecipeItemResponse> recipeResponses = recipeItems.stream()
                .map(item -> toRecipeItemResponse(item, productsById.get(item.getProductId())))
                .toList();

        return DishResponse.builder()
                .id(dish.getId().value())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .ingredients(dish.getIngredients())
                .recipeItems(recipeResponses)
                .availableOrders(calculateAvailableOrders(recipeResponses))
                .createdAt(dish.getCreatedAt())
                .build();
    }

    private DishRecipeItemResponse toRecipeItemResponse(DishRecipeItem item, Product product) {
        BigDecimal requiredQuantity = item.getRequiredQuantity();
        BigDecimal stockLevel = product != null && product.getStockLevel() != null ? product.getStockLevel() : BigDecimal.ZERO;
        String stockUnit = product != null ? product.getUnitOfMeasure() : "";
        String requiredUnit = resolveRecipeUnit(item.getRequiredUnitOfMeasure(), product, requiredQuantity);
        BigDecimal requiredQuantityInStockUnit = convertRecipeQuantityToStockUnit(requiredQuantity, requiredUnit, stockUnit);
        int availableOrders = stockLevel.compareTo(BigDecimal.ZERO) <= 0 || requiredQuantityInStockUnit.compareTo(BigDecimal.ZERO) <= 0
                ? 0
                : stockLevel.divide(requiredQuantityInStockUnit, 0, RoundingMode.DOWN).intValue();
        BigDecimal missingForOneOrder = requiredQuantityInStockUnit.subtract(stockLevel).max(BigDecimal.ZERO);

        return DishRecipeItemResponse.builder()
                .productId(item.getProductId())
                .productName(product != null ? product.getName() : "Producto eliminado")
                .requiredQuantity(requiredQuantity)
                .requiredUnitOfMeasure(requiredUnit)
                .unitOfMeasure(stockUnit)
                .stockUnitOfMeasure(stockUnit)
                .stockLevel(stockLevel)
                .availableOrders(availableOrders)
                .missingForOneOrder(missingForOneOrder)
                .build();
    }

    private Integer calculateAvailableOrders(List<DishRecipeItemResponse> recipeItems) {
        if (recipeItems == null || recipeItems.isEmpty()) {
            return null;
        }

        return recipeItems.stream()
                .map(DishRecipeItemResponse::getAvailableOrders)
                .min(Integer::compareTo)
                .orElse(0);
    }

    private String resolveRecipeUnit(String requestedUnit, Product product, BigDecimal requiredQuantity) {
        if (requestedUnit != null && !requestedUnit.isBlank()) {
            return MeasurementUnitConverter.normalizedLabel(requestedUnit);
        }
        if (product != null && product.getUnitOfMeasure() != null && !product.getUnitOfMeasure().isBlank()) {
            String stockUnit = MeasurementUnitConverter.normalizedLabel(product.getUnitOfMeasure());
            BigDecimal stockLevel = product.getStockLevel() != null ? product.getStockLevel() : BigDecimal.ZERO;
            if (requiredQuantity != null && stockLevel.compareTo(BigDecimal.ZERO) > 0
                    && requiredQuantity.compareTo(stockLevel) > 0) {
                if ("kg".equals(stockUnit)) {
                    return "g";
                }
                if ("l".equals(stockUnit)) {
                    return "ml";
                }
            }
            return stockUnit;
        }
        return "";
    }

    private void validateCompatibleRecipeUnit(String requiredUnit, String stockUnit) {
        try {
            if (!MeasurementUnitConverter.areCompatible(requiredUnit, stockUnit)) {
                throw new ValidationException("recipeItems.requiredUnitOfMeasure", "Recipe unit is not compatible with product unit");
            }
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("recipeItems.requiredUnitOfMeasure", "Recipe unit is not compatible with product unit");
        }
    }

    private BigDecimal convertRecipeQuantityToStockUnit(BigDecimal requiredQuantity, String requiredUnit, String stockUnit) {
        try {
            return MeasurementUnitConverter.convert(requiredQuantity, requiredUnit, stockUnit);
        } catch (IllegalArgumentException exception) {
            return requiredQuantity;
        }
    }
}

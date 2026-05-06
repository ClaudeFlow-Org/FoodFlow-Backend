package com.foodflow.inventory.application;

import com.foodflow.inventory.domain.InventoryCategory;
import com.foodflow.inventory.domain.InventoryCategoryRepository;
import com.foodflow.inventory.domain.InventoryPurchase;
import com.foodflow.inventory.domain.InventoryPurchaseRepository;
import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private static final Logger log = LoggerFactory.getLogger(InventoryApplicationService.class);
    private static final String UNCATEGORIZED = "Sin categoria";

    private final ProductRepository productRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;

    public ProductResponse addProduct(Long userId, ProductRequest request) {
        if (productRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new DuplicateResourceException("Product", "name " + request.getName());
        }

        validateProductRequest(request);

        String category = normalizeCategory(request.getCategory());
        ensureCategoryExists(userId, category);

        Product product = Product.builder()
                .id(Product.ProductId.empty())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .supplier(request.getSupplier())
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : BigDecimal.TEN)
                .stockLevel(request.getStockLevel())
                .unitCost(request.getUnitCost())
                .unitOfMeasure(request.getUnitOfMeasure())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        syncPurchaseHistory("recording initial product purchase", () ->
                recordInventoryPurchase(savedProduct, savedProduct.getStockLevel())
        );

        return toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts(Long userId) {
        return productRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long userId, Long productId) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        return toResponse(product);
    }

    public ProductResponse updateProduct(Long userId, Long productId, ProductRequest request) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByUserIdAndName(userId, request.getName())) {
                throw new DuplicateResourceException("Product", "name " + request.getName());
            }
        }

        validateProductRequest(request);

        BigDecimal previousStockLevel = product.getStockLevel();
        String previousCategory = product.getCategory();
        boolean categoryProvided = request.getCategory() != null;
        String category = categoryProvided ? normalizeCategory(request.getCategory()) : product.getCategory();
        ensureCategoryExists(userId, category);

        product.updateDetails(
                request.getName(),
                request.getDescription(),
                category,
                request.getSupplier(),
                request.getStockLevel(),
                request.getUnitCost(),
                request.getLowStockThreshold(),
                request.getUnitOfMeasure()
        );
        if (categoryProvided) {
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        syncProductPurchaseHistory(userId, productId, updatedProduct, previousStockLevel, previousCategory, request.getStockLevel());

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        productRepository.delete(Product.ProductId.of(productId));
    }

    public List<InventoryCategoryResponse> getCategories(Long userId) {
        return inventoryCategoryRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(InventoryCategory::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toCategoryResponse)
                .toList();
    }

    public InventoryCategoryResponse createCategory(Long userId, InventoryCategoryRequest request) {
        String name = normalizeCategory(request.getName());
        validateCategoryName(name);

        if (inventoryCategoryRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateResourceException("Category", "name " + name);
        }

        InventoryCategory category = InventoryCategory.builder()
                .id(InventoryCategory.InventoryCategoryId.empty())
                .userId(userId)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toCategoryResponse(inventoryCategoryRepository.save(category));
    }

    public InventoryCategoryResponse updateCategory(Long userId, Long categoryId, InventoryCategoryRequest request) {
        InventoryCategory category = inventoryCategoryRepository.findById(InventoryCategory.InventoryCategoryId.of(categoryId))
                .orElseThrow(() -> new NotFoundException("Category", "id " + categoryId));

        if (!category.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this category");
        }

        String newName = normalizeCategory(request.getName());
        validateCategoryName(newName);

        Optional<InventoryCategory> duplicate = inventoryCategoryRepository.findByUserIdAndName(userId, newName);
        if (duplicate.isPresent() && !duplicate.get().getId().value().equals(categoryId)) {
            throw new DuplicateResourceException("Category", "name " + newName);
        }

        String previousName = category.getName();
        category.setName(newName);
        category.setUpdatedAt(LocalDateTime.now());
        InventoryCategory savedCategory = inventoryCategoryRepository.save(category);

        if (!previousName.equalsIgnoreCase(newName)) {
            renameProductsCategory(userId, previousName, newName);
            syncPurchaseHistory("renaming purchase categories", () ->
                    inventoryPurchaseRepository.renameCategory(userId, previousName, newName)
            );
        }

        return toCategoryResponse(savedCategory);
    }

    public void deleteCategory(Long userId, Long categoryId) {
        InventoryCategory category = inventoryCategoryRepository.findById(InventoryCategory.InventoryCategoryId.of(categoryId))
                .orElseThrow(() -> new NotFoundException("Category", "id " + categoryId));

        if (!category.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this category");
        }

        clearProductsCategory(userId, category.getName());
        syncPurchaseHistory("clearing purchase categories", () ->
                inventoryPurchaseRepository.clearCategory(userId, category.getName())
        );
        inventoryCategoryRepository.delete(InventoryCategory.InventoryCategoryId.of(categoryId));
    }

    private void validateProductRequest(ProductRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("name", "Name cannot be empty");
        }
        if (request.getStockLevel() == null || request.getStockLevel().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("stockLevel", "Stock level must be non-negative");
        }
        if (request.getUnitCost() == null || request.getUnitCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("unitCost", "Unit cost must be greater than 0");
        }
        if (request.getUnitOfMeasure() == null || request.getUnitOfMeasure().isBlank()) {
            throw new ValidationException("unitOfMeasure", "Unit of measure cannot be empty");
        }
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId().value())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .supplier(product.getSupplier())
                .lowStockThreshold(product.getLowStockThreshold())
                .stockLevel(product.getStockLevel())
                .unitCost(product.getUnitCost())
                .unitOfMeasure(product.getUnitOfMeasure())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private void recordStockIncrease(Product product, BigDecimal previousStockLevel, BigDecimal newStockLevel) {
        if (previousStockLevel == null || newStockLevel == null) {
            return;
        }

        BigDecimal quantityPurchased = newStockLevel.subtract(previousStockLevel);
        if (quantityPurchased.compareTo(BigDecimal.ZERO) > 0) {
            recordInventoryPurchase(product, quantityPurchased);
        }
    }

    private void recordInventoryPurchase(Product product, BigDecimal quantityPurchased) {
        if (quantityPurchased == null || quantityPurchased.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal unitCost = product.getUnitCost() != null ? product.getUnitCost() : BigDecimal.ZERO;
        InventoryPurchase purchase = InventoryPurchase.builder()
                .id(InventoryPurchase.InventoryPurchaseId.empty())
                .userId(product.getUserId())
                .productId(product.getId().value())
                .productName(product.getName())
                .category(categoryOrDefault(product.getCategory()))
                .quantity(quantityPurchased)
                .unitCost(unitCost)
                .totalCost(unitCost.multiply(quantityPurchased))
                .purchasedAt(LocalDateTime.now())
                .build();

        inventoryPurchaseRepository.save(purchase);
    }

    private void syncProductPurchaseHistory(Long userId, Long productId, Product updatedProduct,
                                            BigDecimal previousStockLevel, String previousCategory,
                                            BigDecimal requestedStockLevel) {
        syncPurchaseHistory("syncing product purchase history", () -> {
            boolean hasPurchaseHistory = inventoryPurchaseRepository.existsByUserIdAndProductId(userId, productId);
            if (hasPurchaseHistory) {
                if (!categoryOrDefault(previousCategory).equalsIgnoreCase(categoryOrDefault(updatedProduct.getCategory()))) {
                    inventoryPurchaseRepository.updateProductCategory(
                            userId,
                            productId,
                            categoryOrDefault(updatedProduct.getCategory())
                    );
                }
                recordStockIncrease(updatedProduct, previousStockLevel, requestedStockLevel);
            } else {
                recordInventoryPurchase(updatedProduct, updatedProduct.getStockLevel());
            }
        });
    }

    private void syncPurchaseHistory(String action, Runnable operation) {
        try {
            operation.run();
        } catch (RuntimeException ex) {
            log.warn("Skipping {} after product data was saved: {}", action, ex.getMessage());
        }
    }

    private void ensureCategoryExists(Long userId, String categoryName) {
        String normalized = normalizeCategory(categoryName);
        if (normalized == null || inventoryCategoryRepository.existsByUserIdAndName(userId, normalized)) {
            return;
        }

        InventoryCategory category = InventoryCategory.builder()
                .id(InventoryCategory.InventoryCategoryId.empty())
                .userId(userId)
                .name(normalized)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        inventoryCategoryRepository.save(category);
    }

    private void renameProductsCategory(Long userId, String previousName, String newName) {
        productRepository.findByUserId(userId).stream()
                .filter(product -> previousName.equalsIgnoreCase(categoryOrDefault(product.getCategory())))
                .forEach(product -> {
                    product.setCategory(newName);
                    product.setUpdatedAt(LocalDateTime.now());
                    productRepository.save(product);
                });
    }

    private void clearProductsCategory(Long userId, String categoryName) {
        productRepository.findByUserId(userId).stream()
                .filter(product -> categoryName.equalsIgnoreCase(categoryOrDefault(product.getCategory())))
                .forEach(product -> {
                    product.setCategory(null);
                    product.setUpdatedAt(LocalDateTime.now());
                    productRepository.save(product);
                });
    }

    private InventoryCategoryResponse toCategoryResponse(InventoryCategory category) {
        return InventoryCategoryResponse.builder()
                .id(category.getId().value())
                .name(category.getName())
                .value(category.getName())
                .label(category.getName())
                .createdAt(category.getCreatedAt())
                .build();
    }

    private void validateCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new ValidationException("name", "Category name cannot be empty");
        }
        if (categoryName.length() > 80) {
            throw new ValidationException("name", "Category name must not exceed 80 characters");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        String trimmed = category.trim();
        return switch (trimmed.toUpperCase()) {
            case "MEAT" -> "Carnes";
            case "VEGETABLES" -> "Vegetales";
            case "DAIRY" -> "Lacteos";
            case "BEVERAGES" -> "Bebidas";
            case "BAKERY" -> "Panaderia";
            case "CONDIMENTS" -> "Condimentos";
            case "GRAINS" -> "Granos";
            case "SNACKS" -> "Snacks";
            case "OTHER" -> "Otro";
            default -> trimmed;
        };
    }

    private String categoryOrDefault(String category) {
        String normalized = normalizeCategory(category);
        return normalized != null ? normalized : UNCATEGORIZED;
    }
}

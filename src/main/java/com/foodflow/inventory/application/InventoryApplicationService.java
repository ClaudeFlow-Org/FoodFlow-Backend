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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private static final Logger log = LoggerFactory.getLogger(InventoryApplicationService.class);
    private static final String UNCATEGORIZED = "Sin categoria";
    private static final String PRODUCT_CATEGORY_ASSIGNMENT_PREFIX = "__pc:";
    private static final int MAX_CATEGORY_NAME_LENGTH = 80;

    private final ProductRepository productRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;

    public ProductResponse addProduct(Long userId, ProductRequest request) {
        if (productRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new DuplicateResourceException("Product", "name " + request.getName());
        }

        validateProductRequest(request);

        String displayCategory = normalizeCategory(request.getCategory());
        runOptionalSync("ensuring product category", () -> ensureCategoryExists(userId, displayCategory));

        Product product = Product.builder()
                .id(Product.ProductId.empty())
                .name(request.getName())
                .description(request.getDescription())
                .category(toProductStorageCategory(displayCategory))
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
        runOptionalSync("syncing product category assignment", () ->
                syncProductCategoryAssignment(userId, savedProduct.getId().value(), displayCategory)
        );
        runOptionalSync("recording initial product purchase", () ->
                recordInventoryPurchase(savedProduct, savedProduct.getStockLevel(), displayCategory)
        );

        return toResponse(savedProduct, displayCategory);
    }

    public List<ProductResponse> getAllProducts(Long userId) {
        Map<Long, String> assignedCategories = getProductCategoryAssignments(userId);
        return productRepository.findByUserId(userId).stream()
                .map(product -> toResponse(product, assignedCategories.get(product.getId().value())))
                .toList();
    }

    public ProductResponse getProductById(Long userId, Long productId) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        return toResponse(product, displayCategoryForProduct(userId, product));
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
        String previousDisplayCategory = displayCategoryForProduct(userId, product);
        boolean categoryProvided = request.getCategory() != null;
        String displayCategory = categoryProvided ? normalizeCategory(request.getCategory()) : previousDisplayCategory;
        String storageCategory = categoryProvided ? toProductStorageCategory(displayCategory) : product.getCategory();
        runOptionalSync("ensuring product category", () -> ensureCategoryExists(userId, displayCategory));

        product.updateDetails(
                request.getName(),
                request.getDescription(),
                storageCategory,
                request.getSupplier(),
                request.getStockLevel(),
                request.getUnitCost(),
                request.getLowStockThreshold(),
                request.getUnitOfMeasure()
        );
        if (categoryProvided) {
            product.setCategory(storageCategory);
        }

        Product updatedProduct = productRepository.save(product);
        if (categoryProvided) {
            runOptionalSync("syncing product category assignment", () ->
                    syncProductCategoryAssignment(userId, productId, displayCategory)
            );
        }
        syncProductPurchaseHistory(userId, productId, updatedProduct, previousStockLevel, previousDisplayCategory, displayCategory, request.getStockLevel());

        return toResponse(updatedProduct, displayCategory);
    }

    public ProductResponse updateProductCategory(Long userId, Long productId, InventoryCategoryRequest request) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        String displayCategory = request != null ? normalizeCategory(request.getName()) : null;
        runOptionalSync("ensuring product category", () -> ensureCategoryExists(userId, displayCategory));

        product.setCategory(toProductStorageCategory(displayCategory));
        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);

        runOptionalSync("syncing product category assignment", () ->
                syncProductCategoryAssignment(userId, productId, displayCategory)
        );
        runOptionalSync("syncing purchase category assignment", () ->
                inventoryPurchaseRepository.updateProductCategory(userId, productId, categoryOrDefault(displayCategory))
        );

        return toResponse(updatedProduct, displayCategory);
    }

    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(Product.ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product", "id " + productId));

        if (!product.getUserId().equals(userId)) {
            throw new ValidationException("You do not have access to this product");
        }

        productRepository.delete(Product.ProductId.of(productId));
        runOptionalSync("clearing product category assignment", () ->
                clearProductCategoryAssignment(userId, productId)
        );
    }

    public List<InventoryCategoryResponse> getCategories(Long userId) {
        return inventoryCategoryRepository.findByUserId(userId).stream()
                .filter(category -> !isProductCategoryAssignment(category.getName()))
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
            runOptionalSync("renaming product category assignments", () ->
                    renameProductCategoryAssignments(userId, previousName, newName)
            );
            runOptionalSync("renaming purchase categories", () ->
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
        runOptionalSync("clearing product category assignments", () ->
                clearProductCategoryAssignmentsByName(userId, category.getName())
        );
        runOptionalSync("clearing purchase categories", () ->
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
        return toResponse(product, null);
    }

    private ProductResponse toResponse(Product product, String assignedCategory) {
        return ProductResponse.builder()
                .id(product.getId().value())
                .name(product.getName())
                .description(product.getDescription())
                .category(displayCategoryOrStorage(product, assignedCategory))
                .supplier(product.getSupplier())
                .lowStockThreshold(product.getLowStockThreshold())
                .stockLevel(product.getStockLevel())
                .unitCost(product.getUnitCost())
                .unitOfMeasure(product.getUnitOfMeasure())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private void recordStockIncrease(Product product, BigDecimal previousStockLevel, BigDecimal newStockLevel, String displayCategory) {
        if (previousStockLevel == null || newStockLevel == null) {
            return;
        }

        BigDecimal quantityPurchased = newStockLevel.subtract(previousStockLevel);
        if (quantityPurchased.compareTo(BigDecimal.ZERO) > 0) {
            recordInventoryPurchase(product, quantityPurchased, displayCategory);
        }
    }

    private void recordInventoryPurchase(Product product, BigDecimal quantityPurchased, String displayCategory) {
        if (quantityPurchased == null || quantityPurchased.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal unitCost = product.getUnitCost() != null ? product.getUnitCost() : BigDecimal.ZERO;
        InventoryPurchase purchase = InventoryPurchase.builder()
                .id(InventoryPurchase.InventoryPurchaseId.empty())
                .userId(product.getUserId())
                .productId(product.getId().value())
                .productName(product.getName())
                .category(categoryOrDefault(displayCategory != null ? displayCategory : product.getCategory()))
                .quantity(quantityPurchased)
                .unitCost(unitCost)
                .totalCost(unitCost.multiply(quantityPurchased))
                .purchasedAt(LocalDateTime.now())
                .build();

        inventoryPurchaseRepository.save(purchase);
    }

    private void syncProductPurchaseHistory(Long userId, Long productId, Product updatedProduct,
                                            BigDecimal previousStockLevel, String previousDisplayCategory,
                                            String displayCategory,
                                            BigDecimal requestedStockLevel) {
        runOptionalSync("syncing product purchase history", () -> {
            boolean hasPurchaseHistory = inventoryPurchaseRepository.existsByUserIdAndProductId(userId, productId);
            if (hasPurchaseHistory) {
                if (!categoryOrDefault(previousDisplayCategory).equalsIgnoreCase(categoryOrDefault(displayCategory))) {
                    inventoryPurchaseRepository.updateProductCategory(
                            userId,
                            productId,
                            categoryOrDefault(displayCategory)
                    );
                }
                recordStockIncrease(updatedProduct, previousStockLevel, requestedStockLevel, displayCategory);
            } else {
                recordInventoryPurchase(updatedProduct, updatedProduct.getStockLevel(), displayCategory);
            }
        });
    }

    private void runOptionalSync(String action, Runnable operation) {
        try {
            operation.run();
        } catch (RuntimeException ex) {
            log.warn("Skipping optional inventory sync while {}: {}", action, ex.getMessage());
        }
    }

    private String displayCategoryOrStorage(Product product, String assignedCategory) {
        String normalizedAssigned = normalizeCategory(assignedCategory);
        if (normalizedAssigned != null) {
            return normalizedAssigned;
        }
        return normalizeCategory(product.getCategory());
    }

    private String displayCategoryForProduct(Long userId, Product product) {
        return findAssignedProductCategory(userId, product.getId().value())
                .orElseGet(() -> normalizeCategory(product.getCategory()));
    }

    private Map<Long, String> getProductCategoryAssignments(Long userId) {
        Map<Long, String> assignments = new HashMap<>();
        try {
            inventoryCategoryRepository.findByUserId(userId).stream()
                    .map(this::parseProductCategoryAssignment)
                    .flatMap(Optional::stream)
                    .forEach(assignment -> assignments.put(assignment.productId(), assignment.categoryName()));
        } catch (RuntimeException ex) {
            log.warn("Skipping product category assignments for user {}: {}", userId, ex.getMessage());
        }
        return assignments;
    }

    private Optional<String> findAssignedProductCategory(Long userId, Long productId) {
        String prefix = productCategoryAssignmentPrefix(productId);
        try {
            return inventoryCategoryRepository.findByUserId(userId).stream()
                    .map(InventoryCategory::getName)
                    .filter(name -> name != null && name.startsWith(prefix))
                    .map(name -> normalizeCategory(name.substring(prefix.length())))
                    .filter(name -> name != null)
                    .findFirst();
        } catch (RuntimeException ex) {
            log.warn("Skipping product category assignment lookup for product {}: {}", productId, ex.getMessage());
            return Optional.empty();
        }
    }

    private void syncProductCategoryAssignment(Long userId, Long productId, String categoryName) {
        clearProductCategoryAssignment(userId, productId);

        String normalized = normalizeCategory(categoryName);
        if (normalized == null) {
            return;
        }

        String assignmentName = productCategoryAssignmentName(productId, normalized);
        InventoryCategory category = InventoryCategory.builder()
                .id(InventoryCategory.InventoryCategoryId.empty())
                .userId(userId)
                .name(assignmentName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        inventoryCategoryRepository.save(category);
    }

    private void clearProductCategoryAssignment(Long userId, Long productId) {
        String prefix = productCategoryAssignmentPrefix(productId);
        inventoryCategoryRepository.findByUserId(userId).stream()
                .filter(category -> category.getName() != null && category.getName().startsWith(prefix))
                .forEach(category -> inventoryCategoryRepository.delete(category.getId()));
    }

    private void renameProductCategoryAssignments(Long userId, String previousName, String newName) {
        String normalizedPrevious = normalizeCategory(previousName);
        String normalizedNew = normalizeCategory(newName);
        if (normalizedPrevious == null || normalizedNew == null) {
            return;
        }

        inventoryCategoryRepository.findByUserId(userId).stream()
                .map(this::parseProductCategoryAssignment)
                .flatMap(Optional::stream)
                .filter(assignment -> normalizedPrevious.equalsIgnoreCase(assignment.categoryName()))
                .forEach(assignment -> syncProductCategoryAssignment(userId, assignment.productId(), normalizedNew));
    }

    private void clearProductCategoryAssignmentsByName(Long userId, String categoryName) {
        String normalizedCategory = normalizeCategory(categoryName);
        if (normalizedCategory == null) {
            return;
        }

        inventoryCategoryRepository.findByUserId(userId).stream()
                .map(this::parseProductCategoryAssignment)
                .flatMap(Optional::stream)
                .filter(assignment -> normalizedCategory.equalsIgnoreCase(assignment.categoryName()))
                .forEach(assignment -> {
                    clearProductCategoryAssignment(userId, assignment.productId());
                    productRepository.findById(Product.ProductId.of(assignment.productId()))
                            .filter(product -> product.getUserId().equals(userId))
                            .ifPresent(product -> {
                                product.setCategory(null);
                                product.setUpdatedAt(LocalDateTime.now());
                                productRepository.save(product);
                            });
                });
    }

    private Optional<ProductCategoryAssignment> parseProductCategoryAssignment(InventoryCategory category) {
        String name = category.getName();
        if (!isProductCategoryAssignment(name)) {
            return Optional.empty();
        }

        int productIdStart = PRODUCT_CATEGORY_ASSIGNMENT_PREFIX.length();
        int categorySeparator = name.indexOf(':', productIdStart);
        if (categorySeparator <= productIdStart || categorySeparator >= name.length() - 1) {
            return Optional.empty();
        }

        try {
            Long productId = Long.valueOf(name.substring(productIdStart, categorySeparator));
            String categoryName = normalizeCategory(name.substring(categorySeparator + 1));
            return categoryName != null
                    ? Optional.of(new ProductCategoryAssignment(productId, categoryName))
                    : Optional.empty();
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private boolean isProductCategoryAssignment(String categoryName) {
        return categoryName != null && categoryName.startsWith(PRODUCT_CATEGORY_ASSIGNMENT_PREFIX);
    }

    private String productCategoryAssignmentPrefix(Long productId) {
        return PRODUCT_CATEGORY_ASSIGNMENT_PREFIX + productId + ":";
    }

    private String productCategoryAssignmentName(Long productId, String categoryName) {
        String prefix = productCategoryAssignmentPrefix(productId);
        int maxNameLength = Math.max(0, MAX_CATEGORY_NAME_LENGTH - prefix.length());
        String normalized = normalizeCategory(categoryName);
        String displayName = normalized != null ? normalized : UNCATEGORIZED;
        if (displayName.length() > maxNameLength) {
            displayName = displayName.substring(0, maxNameLength);
        }
        return prefix + displayName;
    }

    private String toProductStorageCategory(String categoryName) {
        return null;
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
                    product.setCategory(toProductStorageCategory(newName));
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

    private record ProductCategoryAssignment(Long productId, String categoryName) {
    }
}

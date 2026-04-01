package com.foodflow.inventory.application;

import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.ValidationException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final ProductRepository productRepository;

    public ProductResponse addProduct(Long userId, ProductRequest request) {
        if (productRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new DuplicateResourceException("Product", "name " + request.getName());
        }

        validateProductRequest(request);

        Product product = Product.builder()
                .id(Product.ProductId.empty())
                .name(request.getName())
                .stockLevel(request.getStockLevel())
                .unitCost(request.getUnitCost())
                .unitOfMeasure(request.getUnitOfMeasure())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);

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

        product.updateDetails(request.getName(), request.getStockLevel(),
                request.getUnitCost(), request.getUnitOfMeasure());

        Product updatedProduct = productRepository.save(product);

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
                .stockLevel(product.getStockLevel())
                .unitCost(product.getUnitCost())
                .unitOfMeasure(product.getUnitOfMeasure())
                .createdAt(product.getCreatedAt())
                .build();
    }
}

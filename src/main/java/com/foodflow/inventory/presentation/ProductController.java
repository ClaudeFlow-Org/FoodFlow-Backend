package com.foodflow.inventory.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import com.foodflow.inventory.application.InventoryApplicationService;
import com.foodflow.inventory.application.ProductRequest;
import com.foodflow.inventory.application.ProductResponse;
import com.foodflow.inventory.domain.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://foodflowfrontend.vercel.app"})
@Tag(name = "Inventory / Products", description = "APIs for managing inventory products")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final InventoryApplicationService inventoryApplicationService;

    @PostMapping
    @Operation(summary = "Add a new product", description = "Add a new product to inventory with stock level, unit cost, and unit of measure")
    public ApiResponse<ProductResponse> addProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = inventoryApplicationService.addProduct(userAuth.getUserId(), request);
        return ApiResponse.success("Product added successfully", response);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products in the user's inventory")
    public ApiResponse<List<ProductResponse>> getAllProducts(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        List<ProductResponse> response = inventoryApplicationService.getAllProducts(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ApiResponse<ProductResponse> getProductById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        ProductResponse response = inventoryApplicationService.getProductById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product's information")
    public ApiResponse<ProductResponse> updateProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = inventoryApplicationService.updateProduct(userAuth.getUserId(), id, request);
        return ApiResponse.success("Product updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Remove a product from inventory")
    public ApiResponse<Void> deleteProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        inventoryApplicationService.deleteProduct(userAuth.getUserId(), id);
        return ApiResponse.success("Product deleted successfully", null);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get product categories", description = "Retrieve all available product categories")
    public ApiResponse<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(ProductCategory.values())
                .map(cat -> Map.of(
                        "value", cat.name(),
                        "label", cat.getDisplayName(),
                        "labelEs", cat.getDisplayNameEs(),
                        "labelEn", cat.getDisplayNameEn()
                ))
                .collect(Collectors.toList());
        return ApiResponse.success(categories);
    }
}

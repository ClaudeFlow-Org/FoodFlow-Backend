package com.foodflow.inventory.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import com.foodflow.inventory.application.InventoryCategoryRequest;
import com.foodflow.inventory.application.InventoryCategoryResponse;
import com.foodflow.inventory.application.InventoryApplicationService;
import com.foodflow.inventory.application.ProductRequest;
import com.foodflow.inventory.application.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "Get product categories", description = "Retrieve the authenticated user's custom product categories")
    public ApiResponse<List<InventoryCategoryResponse>> getCategories(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        List<InventoryCategoryResponse> categories = inventoryApplicationService.getCategories(userAuth.getUserId());
        return ApiResponse.success(categories);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create product category", description = "Create a custom product category for the authenticated user")
    public ApiResponse<InventoryCategoryResponse> createCategory(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody InventoryCategoryRequest request) {
        InventoryCategoryResponse response = inventoryApplicationService.createCategory(userAuth.getUserId(), request);
        return ApiResponse.success("Category created successfully", response);
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update product category", description = "Rename a custom product category owned by the authenticated user")
    public ApiResponse<InventoryCategoryResponse> updateCategory(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody InventoryCategoryRequest request) {
        InventoryCategoryResponse response = inventoryApplicationService.updateCategory(userAuth.getUserId(), id, request);
        return ApiResponse.success("Category updated successfully", response);
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete product category", description = "Delete a custom product category owned by the authenticated user")
    public ApiResponse<Void> deleteCategory(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        inventoryApplicationService.deleteCategory(userAuth.getUserId(), id);
        return ApiResponse.success("Category deleted successfully", null);
    }
}

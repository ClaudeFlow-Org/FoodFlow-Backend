package com.foodflow.inventory.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import com.foodflow.inventory.application.InventoryApplicationService;
import com.foodflow.inventory.application.ProductRequest;
import com.foodflow.inventory.application.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class ProductController {

    private final InventoryApplicationService inventoryApplicationService;

    @PostMapping
    public ApiResponse<ProductResponse> addProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = inventoryApplicationService.addProduct(userAuth.getUserId(), request);
        return ApiResponse.success("Product added successfully", response);
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        List<ProductResponse> response = inventoryApplicationService.getAllProducts(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        ProductResponse response = inventoryApplicationService.getProductById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = inventoryApplicationService.updateProduct(userAuth.getUserId(), id, request);
        return ApiResponse.success("Product updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        inventoryApplicationService.deleteProduct(userAuth.getUserId(), id);
        return ApiResponse.success("Product deleted successfully", null);
    }
}

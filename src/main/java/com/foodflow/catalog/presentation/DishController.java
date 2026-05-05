package com.foodflow.catalog.presentation;

import com.foodflow.catalog.application.CatalogApplicationService;
import com.foodflow.catalog.application.DishRequest;
import com.foodflow.catalog.application.DishResponse;
import com.foodflow.common.presentation.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.foodflow.identity.infrastructure.UserAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://foodflowfrontend.vercel.app"})
@Tag(name = "Menu / Dishes", description = "APIs for managing restaurant menu dishes")
@SecurityRequirement(name = "Bearer Authentication")
public class DishController {

    private final CatalogApplicationService catalogApplicationService;

    @PostMapping
    @Operation(summary = "Add a new dish", description = "Create a new dish for the restaurant menu. Price must be positive.")
    public ApiResponse<DishResponse> addDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody DishRequest request) {
        DishResponse response = catalogApplicationService.addDish(userAuth.getUserId(), request);
        return ApiResponse.success("Dish added successfully", response);
    }

    @GetMapping
    @Operation(summary = "Get all dishes or search by name", description = "Retrieve all dishes or filter by name using the search parameter")
    public ApiResponse<List<DishResponse>> getAllDishes(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Search term to filter dishes by name")
            @RequestParam(required = false) String search) {
        List<DishResponse> response = catalogApplicationService.searchDishes(userAuth.getUserId(), search);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by ID", description = "Retrieve a specific dish by its ID")
    public ApiResponse<DishResponse> getDishById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Dish ID", required = true)
            @PathVariable Long id) {
        DishResponse response = catalogApplicationService.getDishById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dish", description = "Update an existing dish's information")
    public ApiResponse<DishResponse> updateDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Dish ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody DishRequest request) {
        DishResponse response = catalogApplicationService.updateDish(userAuth.getUserId(), id, request);
        return ApiResponse.success("Dish updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dish", description = "Remove a dish from the menu")
    public ApiResponse<Void> deleteDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Dish ID", required = true)
            @PathVariable Long id) {
        catalogApplicationService.deleteDish(userAuth.getUserId(), id);
        return ApiResponse.success("Dish deleted successfully", null);
    }
}

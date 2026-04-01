package com.foodflow.catalog.presentation;

import com.foodflow.catalog.application.CatalogApplicationService;
import com.foodflow.catalog.application.DishRequest;
import com.foodflow.catalog.application.DishResponse;
import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class DishController {

    private final CatalogApplicationService catalogApplicationService;

    @PostMapping
    public ApiResponse<DishResponse> addDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody DishRequest request) {
        DishResponse response = catalogApplicationService.addDish(userAuth.getUserId(), request);
        return ApiResponse.success("Dish added successfully", response);
    }

    @GetMapping
    public ApiResponse<List<DishResponse>> getAllDishes(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @RequestParam(required = false) String search) {
        List<DishResponse> response = catalogApplicationService.searchDishes(userAuth.getUserId(), search);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<DishResponse> getDishById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        DishResponse response = catalogApplicationService.getDishById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<DishResponse> updateDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id,
            @Valid @RequestBody DishRequest request) {
        DishResponse response = catalogApplicationService.updateDish(userAuth.getUserId(), id, request);
        return ApiResponse.success("Dish updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDish(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        catalogApplicationService.deleteDish(userAuth.getUserId(), id);
        return ApiResponse.success("Dish deleted successfully", null);
    }
}

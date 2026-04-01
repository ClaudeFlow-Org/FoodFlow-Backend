package com.foodflow.sales.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import com.foodflow.sales.application.OrderRequest;
import com.foodflow.sales.application.OrderResponse;
import com.foodflow.sales.application.SalesApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class OrderController {

    private final SalesApplicationService salesApplicationService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = salesApplicationService.createOrder(userAuth.getUserId(), request);
        return ApiResponse.success("Order created successfully", response);
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        List<OrderResponse> response = salesApplicationService.getAllOrders(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        OrderResponse response = salesApplicationService.getOrderById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @PathVariable Long id) {
        salesApplicationService.deleteOrder(userAuth.getUserId(), id);
        return ApiResponse.success("Order deleted successfully", null);
    }
}

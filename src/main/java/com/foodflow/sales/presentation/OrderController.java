package com.foodflow.sales.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import com.foodflow.sales.application.OrderRequest;
import com.foodflow.sales.application.OrderResponse;
import com.foodflow.sales.application.SalesApplicationService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://foodflowfrontend.vercel.app"})
@Tag(name = "Orders", description = "APIs for managing customer orders")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final SalesApplicationService salesApplicationService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order with line items. Total amount is calculated automatically.")
    public ApiResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = salesApplicationService.createOrder(userAuth.getUserId(), request);
        return ApiResponse.success("Order created successfully", response);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders for the authenticated user, ordered chronologically")
    public ApiResponse<List<OrderResponse>> getAllOrders(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        if (userAuth == null) {
            throw new com.foodflow.common.domain.UnauthorizedException("Authentication required");
        }
        List<OrderResponse> response = salesApplicationService.getAllOrders(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ApiResponse<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        OrderResponse response = salesApplicationService.getOrderById(userAuth.getUserId(), id);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Remove an order from the system")
    public ApiResponse<Void> deleteOrder(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        salesApplicationService.deleteOrder(userAuth.getUserId(), id);
        return ApiResponse.success("Order deleted successfully", null);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order to a specific state")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status", required = true)
            @RequestParam com.foodflow.sales.domain.Order.OrderStatus status) {
        OrderResponse response = salesApplicationService.updateOrderStatus(userAuth.getUserId(), id, status);
        return ApiResponse.success("Order status updated successfully", response);
    }

    @PutMapping("/{id}/advance")
    @Operation(summary = "Advance order status", description = "Advance the order to the next status (PENDING -> PREPARING -> READY -> DELIVERED)")
    public ApiResponse<OrderResponse> advanceOrderStatus(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        OrderResponse response = salesApplicationService.advanceOrderStatus(userAuth.getUserId(), id);
        return ApiResponse.success("Order status advanced successfully", response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order that is not yet delivered")
    public ApiResponse<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        OrderResponse response = salesApplicationService.cancelOrder(userAuth.getUserId(), id);
        return ApiResponse.success("Order cancelled successfully", response);
    }
}

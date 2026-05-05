package com.foodflow.billing.presentation;

import com.foodflow.billing.application.BillingApplicationService;
import com.foodflow.billing.application.SubscribeRequest;
import com.foodflow.billing.application.SubscriptionPlanResponse;
import com.foodflow.billing.application.SubscriptionResponse;
import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://foodflowfrontend.vercel.app"})
@Tag(name = "Subscriptions", description = "APIs for managing user subscriptions")
public class SubscriptionController {

    private final BillingApplicationService billingApplicationService;

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans", description = "Retrieve all available subscription plans with their benefits and pricing")
    public ApiResponse<List<SubscriptionPlanResponse>> getPlans() {
        List<SubscriptionPlanResponse> plans = billingApplicationService.getAvailablePlans();
        return ApiResponse.success(plans);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a plan", description = "Create a new subscription for the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ApiResponse<SubscriptionResponse> subscribe(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody SubscribeRequest request) {
        SubscriptionResponse response = billingApplicationService.subscribe(userAuth.getUserId(), request);
        return ApiResponse.success("Subscription created successfully", response);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel the authenticated user's active subscription (access remains until period end)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ApiResponse<SubscriptionResponse> cancel(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        SubscriptionResponse response = billingApplicationService.cancelSubscription(userAuth.getUserId());
        return ApiResponse.success("Subscription cancelled successfully", response);
    }

    @GetMapping("/current")
    @Operation(summary = "Get current subscription", description = "Retrieve the authenticated user's current subscription details")
    @SecurityRequirement(name = "Bearer Authentication")
    public ApiResponse<SubscriptionResponse> getCurrent(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        SubscriptionResponse response = billingApplicationService.getCurrentSubscription(userAuth.getUserId());
        return ApiResponse.success(response);
    }
}

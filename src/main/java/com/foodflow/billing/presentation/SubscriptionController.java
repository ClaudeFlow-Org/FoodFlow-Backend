package com.foodflow.billing.presentation;

import com.foodflow.billing.application.BillingApplicationService;
import com.foodflow.billing.application.SubscribeRequest;
import com.foodflow.billing.application.SubscriptionPlanResponse;
import com.foodflow.billing.application.SubscriptionResponse;
import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class SubscriptionController {

    private final BillingApplicationService billingApplicationService;

    @GetMapping("/plans")
    public ApiResponse<List<SubscriptionPlanResponse>> getPlans() {
        List<SubscriptionPlanResponse> plans = billingApplicationService.getAvailablePlans();
        return ApiResponse.success(plans);
    }

    @PostMapping("/subscribe")
    public ApiResponse<SubscriptionResponse> subscribe(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody SubscribeRequest request) {
        SubscriptionResponse response = billingApplicationService.subscribe(userAuth.getUserId(), request);
        return ApiResponse.success("Subscription created successfully", response);
    }

    @PostMapping("/cancel")
    public ApiResponse<SubscriptionResponse> cancel(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        SubscriptionResponse response = billingApplicationService.cancelSubscription(userAuth.getUserId());
        return ApiResponse.success("Subscription cancelled successfully", response);
    }

    @GetMapping("/current")
    public ApiResponse<SubscriptionResponse> getCurrent(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        SubscriptionResponse response = billingApplicationService.getCurrentSubscription(userAuth.getUserId());
        return ApiResponse.success(response);
    }
}

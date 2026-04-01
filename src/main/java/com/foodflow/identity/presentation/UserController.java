package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.UpdatePasswordRequest;
import com.foodflow.identity.application.UpdateProfileRequest;
import com.foodflow.identity.application.UserResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
@Tag(name = "User Profile", description = "APIs for managing user profile information")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final IdentityApplicationService identityApplicationService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieve the authenticated user's profile information")
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserAuthentication userAuth) {
        UserResponse response = identityApplicationService.getProfile(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update the authenticated user's name and email")
    public ApiResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = identityApplicationService.updateProfile(userAuth.getUserId(), request);
        return ApiResponse.success("Profile updated successfully", response);
    }

    @PutMapping("/password")
    @Operation(summary = "Update password", description = "Update the authenticated user's password")
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody UpdatePasswordRequest request) {
        identityApplicationService.updatePassword(userAuth.getUserId(), request);
        return ApiResponse.success("Password updated successfully", null);
    }
}

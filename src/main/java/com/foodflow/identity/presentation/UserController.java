package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.UpdatePasswordRequest;
import com.foodflow.identity.application.UpdateProfileRequest;
import com.foodflow.identity.application.UserResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class UserController {

    private final IdentityApplicationService identityApplicationService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserAuthentication userAuth) {
        UserResponse response = identityApplicationService.getProfile(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = identityApplicationService.updateProfile(userAuth.getUserId(), request);
        return ApiResponse.success("Profile updated successfully", response);
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Valid @RequestBody UpdatePasswordRequest request) {
        identityApplicationService.updatePassword(userAuth.getUserId(), request);
        return ApiResponse.success("Password updated successfully", null);
    }
}

package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.LoginRequest;
import com.foodflow.identity.application.LoginResponse;
import com.foodflow.identity.application.RegisterRequest;
import com.foodflow.identity.application.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AuthController {

    private final IdentityApplicationService identityApplicationService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = identityApplicationService.register(request);
        return ApiResponse.success("User registered successfully", response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = identityApplicationService.login(request);
        return ApiResponse.success("Login successful", response);
    }
}

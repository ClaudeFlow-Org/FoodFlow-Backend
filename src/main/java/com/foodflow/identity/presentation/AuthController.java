package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.LoginRequest;
import com.foodflow.identity.application.LoginResponse;
import com.foodflow.identity.application.RegisterRequest;
import com.foodflow.identity.application.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://foodflowfrontend.vercel.app"})
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private final IdentityApplicationService identityApplicationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password. Email must be unique.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or email already exists",
                    content = @Content)
    })
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = identityApplicationService.register(request);
        return ApiResponse.success("User registered successfully", response);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate with email and password to receive a JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = identityApplicationService.login(request);
        return ApiResponse.success("Login successful", response);
    }
}

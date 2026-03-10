package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.LoginRequest;
import com.example.foodsdrinks.dto.request.RegisterRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.AuthResponse;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Authentication endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse data = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(messageHelper.get("success.register"), data));
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.ok(messageHelper.get("success.login"), data));
    }

    @Operation(summary = "Get current authenticated user profile",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal String userId) {
        UserResponse data = authService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}

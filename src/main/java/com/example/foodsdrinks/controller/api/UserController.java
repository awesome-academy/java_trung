package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.UpdateProfileRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User endpoints")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse data = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.profile.updated"), data));
    }
}

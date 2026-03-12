package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.AddToCartRequest;
import com.example.foodsdrinks.dto.request.UpdateCartItemRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.CartResponse;
import com.example.foodsdrinks.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Cart endpoints")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Get current user cart")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCart(userId)));
    }

    @Operation(summary = "Add product to cart")
    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.cart.item.added"), cart));
    }

    @Operation(summary = "Update cart item quantity")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal String userId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.cart.item.updated"), cart));
    }

    @Operation(summary = "Remove product from cart")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @AuthenticationPrincipal String userId,
            @PathVariable Long productId) {
        cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.cart.item.removed")));
    }

    @Operation(summary = "Clear entire cart")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.cart.cleared")));
    }
}

package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.CreateOrderRequest;
import com.example.foodsdrinks.dto.request.UpdateOrderStatusRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.OrderResponse;
import com.example.foodsdrinks.dto.response.OrderSummaryResponse;
import com.example.foodsdrinks.dto.response.PageResponse;
import com.example.foodsdrinks.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "Order endpoints")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Place a new order from cart")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.order.created"), order));
    }

    @Operation(summary = "Get my orders")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getMyOrders(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.ok(PageResponse.from(orderService.getMyOrders(userId, pageable))));
    }

    @Operation(summary = "Get order detail")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderDetail(userId, id)));
    }

    @Operation(summary = "Update order status (user: cancel only)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @AuthenticationPrincipal String userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse order = orderService.updateStatus(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.order.status.updated"), order));
    }
}

package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.CreateRatingRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.PageResponse;
import com.example.foodsdrinks.dto.response.RatingResponse;
import com.example.foodsdrinks.service.RatingService;
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

@Tag(name = "Rating", description = "Rating endpoints")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RatingController {

    private final RatingService ratingService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Create a rating for a purchased product")
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> createRating(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateRatingRequest request) {
        RatingResponse rating = ratingService.createRating(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.rating.created"), rating));
    }

    @Operation(summary = "Get ratings by product ID")
    @GetMapping("/products/{productId}/ratings")
    public ResponseEntity<ApiResponse<PageResponse<RatingResponse>>> getRatingsByProductId(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(ratingService.getRatingsByProductId(productId, pageable))));
    }
}

package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.CreateSuggestionRequest;
import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.PageResponse;
import com.example.foodsdrinks.dto.response.SuggestionResponse;
import com.example.foodsdrinks.service.SuggestionService;
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

@Tag(name = "Suggestion", description = "Suggestion endpoints")
@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final MessageHelper messageHelper;

    @Operation(summary = "Create a suggestion")
    @PostMapping
    public ResponseEntity<ApiResponse<SuggestionResponse>> createSuggestion(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateSuggestionRequest request) {
        SuggestionResponse suggestion = suggestionService.createSuggestion(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(messageHelper.get("success.suggestion.created"), suggestion));
    }

    @Operation(summary = "Get my suggestions")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<SuggestionResponse>>> getMySuggestions(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(suggestionService.getMySuggestions(userId, pageable))));
    }
}

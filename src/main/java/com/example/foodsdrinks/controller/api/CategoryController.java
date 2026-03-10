package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.dto.response.ApiResponse;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.dto.response.PageResponse;
import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category", description = "Category endpoints")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAll()));
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getById(id)));
    }

    @Operation(summary = "Get products by category ID")
    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "avgRating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(categoryService.getProductsByCategoryId(id, pageable))));
    }
}

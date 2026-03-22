package com.example.foodsdrinks.fixture;

import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.enums.Classify;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Object Mother for Category-related test data.
 * Centralises fixture creation so every test class shares the same canonical objects.
 */
public final class CategoryMother {

    private CategoryMother() {}

    // ── Entity ──────────────────────────────────────────────────────────────

    /** A persisted "Burger" category with no products. */
    public static Category defaultCategory() {
        return Category.builder()
                .id(1L)
                .name("Burger")
                .classify(Classify.FOOD)
                .description("Burger category")
                .products(Collections.emptyList())
                .build();
    }

    /** A "Pizza" category that already owns at least one product. */
    public static Category categoryWithProducts() {
        return Category.builder()
                .id(2L)
                .name("Pizza")
                .classify(Classify.FOOD)
                .description("Pizza category")
                .products(List.of(new Product()))
                .build();
    }

    // ── Response DTO ────────────────────────────────────────────────────────

    /** The expected response that maps from {@link #defaultCategory()}. */
    public static CategoryResponse defaultCategoryResponse() {
        return CategoryResponse.builder()
                .id(1L)
                .name("Burger")
                .classify(Classify.FOOD)
                .description("Burger category")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── Request DTO ─────────────────────────────────────────────────────────

    /** A valid create-request for the default "Burger" category. */
    public static CategoryRequest createRequest() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Burger");
        request.setClassify(Classify.FOOD);
        request.setDescription("Burger category");
        return request;
    }

    /**
     * A valid update-request that intentionally uses a <em>different</em> name ("Pizza")
     * so tests can verify that the mapper actually mutates the entity.
     */
    public static CategoryRequest updateRequest() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Pizza");
        request.setClassify(Classify.FOOD);
        request.setDescription("Pizza category");
        return request;
    }
}

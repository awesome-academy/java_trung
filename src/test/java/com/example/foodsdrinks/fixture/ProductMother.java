package com.example.foodsdrinks.fixture;

import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.entity.enums.Classify;

import java.math.BigDecimal;

/**
 * Object Mother for Product-related test data.
 */
public final class ProductMother {

    private ProductMother() {}

    // ── Response DTO ────────────────────────────────────────────────────────

    /** A "Cheese Burger" product that belongs to the default "Burger" category (id=1). */
    public static ProductResponse defaultProductResponse() {
        return ProductResponse.builder()
                .id(10L)
                .name("Cheese Burger")
                .price(BigDecimal.valueOf(9.99))
                .categoryId(1L)
                .categoryName("Burger")
                .classify(Classify.FOOD)
                .build();
    }
}

package com.example.foodsdrinks.dto.response;

import com.example.foodsdrinks.entity.enums.Classify;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean available;
    private BigDecimal avgRating;
    private int ratingCount;
    private Long categoryId;
    private String categoryName;
    private Classify classify;
    private LocalDateTime createdAt;
}

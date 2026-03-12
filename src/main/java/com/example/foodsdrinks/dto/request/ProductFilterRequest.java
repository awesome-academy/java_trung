package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.Classify;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductFilterRequest {
    private String search;
    private Long categoryId;
    private Classify classify;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean available;
}

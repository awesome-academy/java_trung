package com.example.foodsdrinks.dto.response;

import com.example.foodsdrinks.entity.enums.Classify;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private Classify classify;
    private String description;
    private LocalDateTime createdAt;
}

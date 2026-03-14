package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.Classify;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Classify is required")
    private Classify classify;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}

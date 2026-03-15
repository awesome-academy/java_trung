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

    @NotBlank(message = "{validation.category.name.required}")
    @Size(max = 100, message = "{validation.category.name.size}")
    private String name;

    @NotNull(message = "{validation.category.classify.required}")
    private Classify classify;

    @Size(max = 1000, message = "{validation.category.description.size}")
    private String description;
}

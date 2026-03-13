package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.Classify;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSuggestionRequest {

    @NotBlank(message = "{validation.suggestion.name.required}")
    @Size(max = 200, message = "{validation.suggestion.name.size}")
    private String name;

    @NotNull(message = "{validation.suggestion.classify.required}")
    private Classify classify;

    @Size(max = 1000, message = "{validation.suggestion.description.size}")
    private String description;
}

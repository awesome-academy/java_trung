package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectSuggestionRequest {

    @NotBlank(message = "{validation.admin.note.required}")
    @Size(max = 1000, message = "{validation.admin.note.size}")
    private String adminNote;
}

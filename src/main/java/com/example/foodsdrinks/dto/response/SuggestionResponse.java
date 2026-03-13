package com.example.foodsdrinks.dto.response;

import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SuggestionResponse {

    private Long id;
    private String userId;
    private String name;
    private Classify classify;
    private String description;
    private SuggestionStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
}

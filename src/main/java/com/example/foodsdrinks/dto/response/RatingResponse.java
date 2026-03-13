package com.example.foodsdrinks.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RatingResponse {

    private Long id;
    private String userId;
    private Long productId;
    private Byte score;
    private String comment;
    private LocalDateTime createdAt;
}

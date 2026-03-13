package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRatingRequest {

    @NotNull(message = "{validation.product.id.required}")
    private Long productId;

    @NotNull(message = "{validation.rating.score.required}")
    @Min(value = 1, message = "{validation.rating.score.min}")
    @Max(value = 5, message = "{validation.rating.score.max}")
    private Byte score;

    @Size(max = 1000, message = "{validation.rating.comment.size}")
    private String comment;
}

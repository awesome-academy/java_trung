package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "{validation.product.id.required}")
    private Long productId;

    @Min(value = 1, message = "{validation.quantity.min}")
    private int quantity = 1;
}

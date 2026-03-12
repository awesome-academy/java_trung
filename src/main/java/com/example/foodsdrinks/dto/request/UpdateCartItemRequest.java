package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequest {

    @Min(value = 1, message = "{validation.quantity.min}")
    private int quantity;
}

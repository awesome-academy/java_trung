package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank(message = "{validation.delivery.address.required}")
    @Size(max = 500, message = "{validation.delivery.address.size}")
    private String deliveryAddress;

    @Size(max = 1000, message = "{validation.note.size}")
    private String note;
}

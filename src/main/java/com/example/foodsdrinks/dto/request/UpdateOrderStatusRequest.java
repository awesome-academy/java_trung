package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "{validation.status.required}")
    private OrderStatus status;
}

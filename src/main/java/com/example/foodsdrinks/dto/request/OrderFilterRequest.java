package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFilterRequest {
    private OrderStatus status;
    private String keyword;
}

package com.example.foodsdrinks.dto.response;

import com.example.foodsdrinks.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderSummaryResponse {

    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String note;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

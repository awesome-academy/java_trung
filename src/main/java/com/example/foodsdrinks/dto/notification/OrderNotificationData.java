package com.example.foodsdrinks.dto.notification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderNotificationData(
        Long orderId,
        String userEmail,
        BigDecimal totalAmount,
        String deliveryAddress,
        LocalDateTime createdAt,
        List<Item> items
) {
    public record Item(
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}
}

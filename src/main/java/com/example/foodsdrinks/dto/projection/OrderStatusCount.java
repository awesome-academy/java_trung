package com.example.foodsdrinks.dto.projection;

import com.example.foodsdrinks.entity.enums.OrderStatus;

/**
 * Spring Data interface projection for ORDER COUNT grouped by status.
 * Aliases in JPQL: status, count  →  getStatus(), getCount()
 */
public interface OrderStatusCount {
    OrderStatus getStatus();
    Long getCount();
}

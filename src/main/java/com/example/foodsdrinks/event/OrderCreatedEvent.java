package com.example.foodsdrinks.event;

import com.example.foodsdrinks.dto.notification.OrderNotificationData;

import java.util.List;

/**
 * Published after an order is persisted (within the committing transaction).
 * Consumed by {@link com.example.foodsdrinks.event.OrderNotificationListener} after the transaction commits.
 */
public record OrderCreatedEvent(OrderNotificationData orderData, List<String> adminEmails) {}

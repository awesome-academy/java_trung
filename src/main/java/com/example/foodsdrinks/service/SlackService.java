package com.example.foodsdrinks.service;

import com.example.foodsdrinks.config.DateTimeFormatterConstants;
import com.example.foodsdrinks.dto.notification.OrderNotificationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
public class SlackService {

    @Value("${app.slack.webhook-url}")
    private String webhookUrl;

    private final RestClient restClient = RestClient.create();

    public void sendOrderNotification(OrderNotificationData order) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("🛒 New Order #%d%n", order.orderId()));
        messageBuilder.append(String.format("👤 User: %s%n", order.userEmail()));
        messageBuilder.append(String.format("💰 Total: %s%n", order.totalAmount()));
        messageBuilder.append(String.format("📍 Delivery Address: %s%n", order.deliveryAddress()));
        messageBuilder.append(String.format("🕐 Created At: %s%n", order.createdAt().format(DateTimeFormatterConstants.DATE_TIME_FORMATTER)));
        messageBuilder.append(String.format("📦 Items: %d item(s)%n", order.items().size()));
        order.items().forEach(item ->
                messageBuilder.append(String.format("  • %s — qty: %d, price: %s, subtotal: %s%n",
                        item.productName(),
                        item.quantity(),
                        item.unitPrice(),
                        item.subtotal()))
        );

        String message = messageBuilder.toString();

        try {
            Map<String, String> payload = Map.of("text", message);
            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Slack notification sent for order #{}", order.orderId());
        } catch (Exception e) {
            log.warn("Failed to send Slack notification for order #{}: {}", order.orderId(), e.getMessage());
        }
    }
}

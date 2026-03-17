package com.example.foodsdrinks.service;

import com.example.foodsdrinks.entity.Order;
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

    @Value("${app.slack.enabled:false}")
    private boolean enabled;

    private final RestClient restClient = RestClient.create();

    public void sendOrderNotification(Order order) {
        if (!enabled) {
            log.debug("Slack notifications disabled — skipping order #{}", order.getId());
            return;
        }

        String message = String.format(
                "🛒 New Order #%d%n👤 User: %s%n💰 Total: %s%n📦 Items: %d item(s)%n📍 Address: %s",
                order.getId(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getOrderItems().size(),
                order.getDeliveryAddress()
        );

        try {
            Map<String, String> payload = Map.of("text", message);
            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Slack notification sent for order #{}", order.getId());
        } catch (Exception e) {
            log.warn("Failed to send Slack notification for order #{}: {}", order.getId(), e.getMessage());
        }
    }
}

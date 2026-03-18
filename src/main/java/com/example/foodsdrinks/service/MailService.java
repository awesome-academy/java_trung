package com.example.foodsdrinks.service;

import com.example.foodsdrinks.config.DateTimeFormatterConstants;
import com.example.foodsdrinks.dto.notification.OrderNotificationData;
import com.example.foodsdrinks.dto.report.MonthlyReportData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${app.mail.from}")
    private String mailFrom;

    private final JavaMailSender mailSender;

    public void sendOrderNotificationToAdmins(OrderNotificationData order, List<String> adminEmails) {
        if (adminEmails == null || adminEmails.isEmpty()) {
            log.debug("No admin emails provided — skipping order notification mail for order #{}", order.orderId());
            return;
        }

        String subject = String.format("New Order #%d - Foods & Drinks", order.orderId());

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(String.format("New order received%n"));
        bodyBuilder.append(String.format("Order ID: #%d%n", order.orderId()));
        bodyBuilder.append(String.format("User: %s%n", order.userEmail()));
        bodyBuilder.append(String.format("Total: %s%n", order.totalAmount()));
        bodyBuilder.append(String.format("Delivery Address: %s%n", order.deliveryAddress()));
        bodyBuilder.append(String.format("Created At: %s%n", order.createdAt().format(DateTimeFormatterConstants.DATE_TIME_FORMATTER)));
        bodyBuilder.append("\nItems:\n");
        order.items().forEach(item ->
                bodyBuilder.append(String.format("  - %s | qty: %d | price: %s | subtotal: %s%n",
                        item.productName(),
                        item.quantity(),
                        item.unitPrice(),
                        item.subtotal()))
        );

        String body = bodyBuilder.toString();

        for (String adminEmail : adminEmails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(adminEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Order notification email sent to {} for order #{}", adminEmail, order.orderId());
            } catch (Exception e) {
                log.warn("Failed to send order notification email to {} for order #{}: {}", adminEmail, order.orderId(), e.getMessage());
            }
        }
    }

    public void sendMonthlyReport(List<String> adminEmails, MonthlyReportData data) {
        if (adminEmails == null || adminEmails.isEmpty()) {
            log.debug("No admin emails provided — skipping monthly report mail for {}", data.getMonthYear());
            return;
        }

        String subject = String.format("Monthly Report - %s", data.getMonthYear());

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(String.format("Monthly Report: %s%n", data.getMonthYear()));
        bodyBuilder.append("─────────────────────────\n");
        bodyBuilder.append(String.format("Total Orders (DONE): %d%n", data.getTotalOrders()));
        bodyBuilder.append(String.format("Total Revenue: %s%n", data.getTotalRevenue()));
        bodyBuilder.append("\nTop 5 Products:\n");

        List<String> topProducts = data.getTopProducts();
        for (int i = 0; i < topProducts.size(); i++) {
            bodyBuilder.append(String.format("%d. %s%n", i + 1, topProducts.get(i)));
        }

        String body = bodyBuilder.toString();

        for (String adminEmail : adminEmails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(adminEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Monthly report email sent to {} for {}", adminEmail, data.getMonthYear());
            } catch (Exception e) {
                log.warn("Failed to send monthly report email to {} for {}: {}", adminEmail, data.getMonthYear(), e.getMessage());
            }
        }
    }
}

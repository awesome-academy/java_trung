package com.example.foodsdrinks.event;

import com.example.foodsdrinks.service.MailService;
import com.example.foodsdrinks.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Handles order-created notifications <em>after</em> the creating transaction has committed.
 *
 * <p>Runs <strong>asynchronously</strong> on a separate thread via {@code @Async}.
 * Using {@link TransactionPhase#AFTER_COMMIT} ensures notifications are only sent
 * when the order is actually persisted in the database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final SlackService slackService;
    private final MailService mailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.debug("Sending notifications for order #{}", event.order().getId());
        slackService.sendOrderNotification(event.order());
        mailService.sendOrderNotificationToAdmins(event.order(), event.adminEmails());
    }
}

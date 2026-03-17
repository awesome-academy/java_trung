package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.MonthlyReportData;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.entity.enums.Role;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.repository.OrderItemRepository;
import com.example.foodsdrinks.repository.OrderRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyReportScheduler {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 0 8 L * ?")
    @Transactional(readOnly = true)
    public void run() {
        log.info("Monthly report scheduler started");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime to = now;

        String monthYear = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        // Total orders DONE
        List<?> orders = orderRepository.findAllByStatusAndUpdatedAtBetween(OrderStatus.DONE, from, to);
        long totalOrders = orders.size();

        // Total revenue
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatusAndUpdatedAtBetween(
                OrderStatus.DONE, from, to);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Top 5 products
        List<Object[]> topProductRows = orderItemRepository.findTopProductsByStatusAndPeriod(
                OrderStatus.DONE, from, to, Pageable.ofSize(5));

        List<String> topProducts = topProductRows.stream()
                .map(row -> String.format("%s - %s sold", row[0], row[1]))
                .toList();

        MonthlyReportData data = MonthlyReportData.builder()
                .monthYear(monthYear)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .topProducts(topProducts)
                .build();

        List<String> adminEmails = userRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(User::getEmail)
                .toList();

        mailService.sendMonthlyReport(adminEmails, data);

        log.info("Monthly report sent for {} — orders: {}, revenue: {}", monthYear, totalOrders, totalRevenue);
    }
}

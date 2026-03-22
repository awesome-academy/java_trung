package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.projection.TopProductSales;
import com.example.foodsdrinks.dto.report.MonthlyReportData;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.entity.enums.Role;
import com.example.foodsdrinks.repository.OrderItemRepository;
import com.example.foodsdrinks.repository.OrderRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Transactional(readOnly = true)
    public void generateAndSend() {
        log.info("Monthly report scheduler started");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        String monthYear = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        // Total orders DONE
        List<?> orders = orderRepository.findAllByStatusAndUpdatedAtBetween(OrderStatus.DONE, from, now);
        long totalOrders = orders.size();

        // Total revenue
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatusAndUpdatedAtBetween(
                OrderStatus.DONE, from, now);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Top 5 products
        List<TopProductSales> topProductRows = orderItemRepository.findTopProductsByStatusAndPeriod(
                OrderStatus.DONE, from, now, Pageable.ofSize(5));

        List<String> topProducts = topProductRows.stream()
                .map(row -> String.format("%s - %s sold", row.getProductName(), row.getTotalQuantity()))
                .toList();

        MonthlyReportData data = MonthlyReportData.builder()
                .monthYear(monthYear)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .topProducts(topProducts)
                .build();

        List<String> adminEmails = userRepository.findAllByRoleAndActiveTrue(Role.ADMIN)
                .stream()
                .map(User::getEmail)
                .toList();

        mailService.sendMonthlyReport(adminEmails, data);

        log.info("Monthly report sent for {} — orders: {}, revenue: {}", monthYear, totalOrders, totalRevenue);
    }
}

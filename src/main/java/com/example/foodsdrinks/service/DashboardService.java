package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.projection.MonthlyRevenue;
import com.example.foodsdrinks.dto.projection.OrderStatusCount;
import com.example.foodsdrinks.dto.projection.TopProductSales;
import com.example.foodsdrinks.dto.response.DashboardStatsResponse;
import com.example.foodsdrinks.dto.response.DashboardStatsResponse.ChartData;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.repository.OrderItemRepository;
import com.example.foodsdrinks.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        log.info("Building dashboard stats");
        return new DashboardStatsResponse(
                buildRevenueByMonth(),
                buildOrdersByStatus(),
                buildTopProducts()
        );
    }

    private ChartData buildRevenueByMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        LocalDateTime from = today.minusMonths(5)
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
        LocalDateTime to = today
                .with(TemporalAdjusters.firstDayOfNextMonth())
                .atStartOfDay();

        Map<YearMonth, BigDecimal> revenueMap = orderRepository
                .sumRevenueGroupByMonth(OrderStatus.DONE, from, to)
                .stream()
                .collect(Collectors.toMap(
                        row -> YearMonth.of(row.getYear(), row.getMonth()),
                        MonthlyRevenue::getRevenue
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = currentMonth.minusMonths(i);
            labels.add(ym.atDay(1).format(formatter));
            data.add(revenueMap.getOrDefault(ym, BigDecimal.ZERO));
        }

        return new ChartData(labels, data);
    }

    private ChartData buildOrdersByStatus() {
        List<OrderStatusCount> rows = orderRepository.countGroupByStatus();

        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (OrderStatusCount row : rows) {
            labels.add(row.getStatus().toString());
            data.add(row.getCount());
        }

        return new ChartData(labels, data);
    }

    private ChartData buildTopProducts() {
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<TopProductSales> rows = orderItemRepository.findTopProductsByStatusAndPeriod(
                OrderStatus.DONE, monthStart, now, Pageable.ofSize(5));

        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (TopProductSales row : rows) {
            labels.add(row.getProductName());
            data.add(row.getTotalQuantity());
        }

        return new ChartData(labels, data);
    }
}

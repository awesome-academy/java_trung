package com.example.foodsdrinks.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class MonthlyReportData {
    private String monthYear;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private List<String> topProducts;
}

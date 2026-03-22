package com.example.foodsdrinks.dto.response;

import java.util.List;

public record DashboardStatsResponse(
        ChartData revenueByMonth,
        ChartData ordersByStatus,
        ChartData topProducts) {

    public record ChartData(List<String> labels, List<Number> data) {}
}

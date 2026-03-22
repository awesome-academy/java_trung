package com.example.foodsdrinks.dto.projection;

import java.math.BigDecimal;

/**
 * Spring Data interface projection for REVENUE grouped by month.
 * Aliases in JPQL: year, month, revenue  →  getYear(), getMonth(), getRevenue()
 */
public interface MonthlyRevenue {
    Integer getYear();
    Integer getMonth();
    BigDecimal getRevenue();
}

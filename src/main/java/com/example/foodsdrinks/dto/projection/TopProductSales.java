package com.example.foodsdrinks.dto.projection;

/**
 * Spring Data interface projection for TOP-SELLING products.
 * Aliases in JPQL: productName, totalQuantity  →  getProductName(), getTotalQuantity()
 */
public interface TopProductSales {
    String getProductName();
    Long getTotalQuantity();
}

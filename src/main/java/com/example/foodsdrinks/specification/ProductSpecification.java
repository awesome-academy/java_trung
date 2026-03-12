package com.example.foodsdrinks.specification;

import com.example.foodsdrinks.entity.Category_;
import com.example.foodsdrinks.entity.Product_;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.enums.Classify;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> filter(
            String search,
            Long categoryId,
            Classify classify,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get(Product_.name)), keyword));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get(Product_.category).get(Category_.id), categoryId));
            }

            if (classify != null) {
                predicates.add(cb.equal(root.get(Product_.classify), classify));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Product_.price), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Product_.price), maxPrice));
            }

            if (available != null) {
                predicates.add(cb.equal(root.get(Product_.available), available));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

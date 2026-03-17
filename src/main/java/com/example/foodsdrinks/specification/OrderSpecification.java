package com.example.foodsdrinks.specification;

import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.Order_;
import com.example.foodsdrinks.entity.User_;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> filter(OrderStatus status, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get(Order_.status), status));
            }

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get(Order_.user).get(User_.email)), pattern),
                        cb.like(cb.lower(root.get(Order_.user).get(User_.fullName)), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

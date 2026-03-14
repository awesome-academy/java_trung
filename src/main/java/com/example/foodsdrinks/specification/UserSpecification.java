package com.example.foodsdrinks.specification;

import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> filter(String email, Role role, Boolean active) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (email != null && !email.isBlank()) {
                String keyword = "%" + email.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("email")), keyword));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package com.example.foodsdrinks.specification;

import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.User_;
import com.example.foodsdrinks.entity.enums.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> filter(String keyword, Role role, Boolean active) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get(User_.email)), pattern),
                        cb.like(cb.lower(root.get(User_.fullName)), pattern)
                ));
            }

            if (role != null) {
                predicates.add(cb.equal(root.get(User_.role), role));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get(User_.active), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

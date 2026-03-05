package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUserId(String userId, Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
}

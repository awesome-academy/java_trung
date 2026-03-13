package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.OrderItem;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrderId(Long orderId);

    Optional<OrderItem> findFirstByOrderUserIdAndProductIdAndOrderStatus(
            String userId, Long productId, OrderStatus status);
}

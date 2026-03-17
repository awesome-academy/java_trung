package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.OrderItem;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrderId(Long orderId);

    Optional<OrderItem> findFirstByOrderUserIdAndProductIdAndOrderStatus(
            String userId, Long productId, OrderStatus status);

    boolean existsByProductId(Long productId);

    @Query("""
        SELECT oi.product.name, SUM(oi.quantity) as totalQty
        FROM OrderItem oi
        WHERE oi.order.status = :status
        AND oi.order.updatedAt >= :from
        AND oi.order.updatedAt < :to
        GROUP BY oi.product.id, oi.product.name
        ORDER BY totalQty DESC
    """)
    List<Object[]> findTopProductsByStatusAndPeriod(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}

package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findAllByUserId(String userId, Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<Order> findAll(@Nullable Specification<Order> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Optional<Order> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.product"})
    Optional<Order> findWithDetailsById(Long id);

    @Query("""
        SELECT o FROM Order o
        WHERE o.status = :status
        AND o.updatedAt >= :from
        AND o.updatedAt < :to
    """)
    List<Order> findAllByStatusAndUpdatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o
        WHERE o.status = :status
        AND o.updatedAt >= :from
        AND o.updatedAt < :to
    """)
    BigDecimal sumTotalAmountByStatusAndUpdatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}

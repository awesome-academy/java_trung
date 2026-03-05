package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByUserId(String userId);

    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    boolean existsByUserIdAndProductId(String userId, Long productId);

    void deleteAllByUserId(String userId);
}

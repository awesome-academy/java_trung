package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByUserIdAndProductId(String userId, Long productId);


    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.product.id = :productId")
    Optional<Double> findAvgScoreByProductId(@Param("productId") Long productId);

    long countByProductId(Long productId);

    Page<Rating> findAllByProductId(Long productId, Pageable pageable);
}

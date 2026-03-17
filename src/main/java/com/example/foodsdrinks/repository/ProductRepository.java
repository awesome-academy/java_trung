package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(@Nullable Specification<Product> spec, @NonNull Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
            set p.stock = p.stock - :qty
            where p.id = :productId
              and p.available = true
              and p.stock >= :qty
            """)
    int decrementStock(@Param("productId") Long productId, @Param("qty") int qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
            set p.stock = p.stock + :qty
            where p.id = :productId
            """)
    int incrementStock(@Param("productId") Long productId, @Param("qty") int qty);
}

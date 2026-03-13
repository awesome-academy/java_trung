package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

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

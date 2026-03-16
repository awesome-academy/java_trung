package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    List<Category> findAllByClassify(Classify classify);

    boolean existsByNameAndClassify(String name, Classify classify);

    boolean existsByNameAndClassifyAndIdNot(String name, Classify classify, Long id);
}

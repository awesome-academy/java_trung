package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Suggestion;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    Page<Suggestion> findAllByStatus(SuggestionStatus status, Pageable pageable);

    Page<Suggestion> findAllByUserId(String userId, Pageable pageable);
}

package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.Suggestion;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Suggestion> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Suggestion> findAllByStatus(SuggestionStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Suggestion> findAllByUserId(String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Suggestion> findById(Long id);
}

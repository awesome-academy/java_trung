package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.CreateSuggestionRequest;
import com.example.foodsdrinks.dto.response.SuggestionResponse;
import com.example.foodsdrinks.entity.Suggestion;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.SuggestionMapper;
import com.example.foodsdrinks.repository.SuggestionRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final SuggestionMapper suggestionMapper;

    public SuggestionResponse createSuggestion(String userId, CreateSuggestionRequest request) {
        User userRef = userRepository.getReferenceById(userId);

        Suggestion suggestion = Suggestion.builder()
                .user(userRef)
                .name(request.getName())
                .classify(request.getClassify())
                .description(request.getDescription())
                .status(SuggestionStatus.PENDING)
                .build();

        Suggestion savedSuggestion = suggestionRepository.save(suggestion);
        return suggestionMapper.toResponse(savedSuggestion);
    }

    @Transactional(readOnly = true)
    public Page<SuggestionResponse> getMySuggestions(String userId, Pageable pageable) {
        return suggestionRepository.findAllByUserId(userId, pageable).map(suggestionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<Suggestion> getSuggestions(SuggestionStatus status, Pageable pageable) {
        if (status != null) {
            return suggestionRepository.findAllByStatus(status, pageable);
        }
        return suggestionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Suggestion getSuggestionDetail(Long id) {
        return suggestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUGGESTION_NOT_FOUND));
    }

    public void approve(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUGGESTION_NOT_FOUND));
        suggestion.setStatus(SuggestionStatus.APPROVED);
    }

    public void reject(Long id, String adminNote) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUGGESTION_NOT_FOUND));
        suggestion.setStatus(SuggestionStatus.REJECTED);
        suggestion.setAdminNote(adminNote);
    }
}

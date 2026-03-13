package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.SuggestionResponse;
import com.example.foodsdrinks.entity.Suggestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SuggestionMapper {

    @Mapping(target = "userId", source = "user.id")
    SuggestionResponse toResponse(Suggestion suggestion);
}

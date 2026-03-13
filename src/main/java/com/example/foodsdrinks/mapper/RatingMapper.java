package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.RatingResponse;
import com.example.foodsdrinks.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    RatingResponse toResponse(Rating rating);
}

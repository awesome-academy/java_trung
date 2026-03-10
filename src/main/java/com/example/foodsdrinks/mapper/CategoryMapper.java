package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}

package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    CategoryRequest toRequest(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}

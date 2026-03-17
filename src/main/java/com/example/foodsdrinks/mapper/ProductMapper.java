package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.request.ProductRequest;
import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.entity.Product;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "imageFile", ignore = true)
    ProductRequest toRequest(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "avgRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "available", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);

    @InheritConfiguration(name = "updateEntityFromRequest")
    Product toEntity(ProductRequest request);
}

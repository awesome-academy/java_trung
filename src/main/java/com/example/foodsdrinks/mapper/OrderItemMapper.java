package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.OrderItemResponse;
import com.example.foodsdrinks.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    OrderItemResponse toResponse(OrderItem orderItem);
}

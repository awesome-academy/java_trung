package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.CartItemResponse;
import com.example.foodsdrinks.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", source = "subtotal")
    CartItemResponse toResponse(CartItem cartItem);
}

package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.OrderResponse;
import com.example.foodsdrinks.dto.response.OrderSummaryResponse;
import com.example.foodsdrinks.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    OrderResponse toResponse(Order order);

    OrderSummaryResponse toSummaryResponse(Order order);
}

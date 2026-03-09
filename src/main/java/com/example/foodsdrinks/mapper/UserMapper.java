package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "isActive", source = "active")
    UserResponse toResponse(User user);
}

package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.request.UpdateProfileRequest;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateProfileRequest request, @MappingTarget User user);
}

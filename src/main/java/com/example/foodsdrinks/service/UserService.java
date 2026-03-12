package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.UpdateProfileRequest;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.UserMapper;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateEntity(request, user);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}

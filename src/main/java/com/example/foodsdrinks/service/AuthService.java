package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.LoginRequest;
import com.example.foodsdrinks.dto.request.RegisterRequest;
import com.example.foodsdrinks.dto.response.AuthResponse;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.UserMapper;
import com.example.foodsdrinks.repository.UserRepository;
import com.example.foodsdrinks.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .build();

        User savedUser = userRepository.save(user);

        log.info("New user registered: {}", savedUser.getEmail());

        String token = jwtUtil.generateAccessToken(
                savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        return AuthResponse.of(token, userMapper.toResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        log.info("User logged in: {}", user.getEmail());
        return AuthResponse.of(token, userMapper.toResponse(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
}

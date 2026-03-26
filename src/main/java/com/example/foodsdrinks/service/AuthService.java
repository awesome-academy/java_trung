package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.*;
import com.example.foodsdrinks.dto.response.AuthResponse;
import com.example.foodsdrinks.dto.response.UserResponse;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.UserSocialAccount;
import com.example.foodsdrinks.entity.enums.AuthProvider;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.UserMapper;
import com.example.foodsdrinks.provider.SocialLoginProvider;
import com.example.foodsdrinks.provider.SocialProfile;
import com.example.foodsdrinks.repository.UserRepository;
import com.example.foodsdrinks.repository.UserSocialAccountRepository;
import com.example.foodsdrinks.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    private final Map<String, SocialLoginProvider> socialLoginProviders;

    // ── Local auth ───────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        var user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .authProvider(AuthProvider.LOCAL)
                .build();

        var savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        var token = jwtUtil.generateAccessToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        return AuthResponse.of(token, userMapper.toResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        if (user.getPassword() == null) {
            throw new AppException(ErrorCode.SOCIAL_ONLY_ACCOUNT);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        var token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("User logged in: {}", user.getEmail());
        return AuthResponse.of(token, userMapper.toResponse(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    // ── Social login ─────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse handleSocialLogin(SocialLoginRequest request) {
        var providerKey = request.provider().toUpperCase();
        var provider    = resolveProvider(providerKey);
        var profile     = provider.verifyToken(request.code());

        var user = userRepository.findByEmail(profile.email())
                .map(existing -> processExistingUserSocialLogin(existing, providerKey, profile))
                .orElseGet(() -> createSocialUser(profile, providerKey));

        var token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("Social login successful – user={} provider={}", user.getEmail(), providerKey);
        return AuthResponse.of(token, userMapper.toResponse(user));
    }

    @Transactional
    public UserResponse linkAccount(String userId, SocialLoginRequest request) {
        var user        = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var providerKey = request.provider().toUpperCase();
        var provider    = resolveProvider(providerKey);
        var profile     = provider.verifyToken(request.code());

        if (socialAccountRepository.existsByProviderNameAndProviderUserIdAndUserIdNot(
                providerKey, profile.providerId(), userId)) {
            throw new AppException(ErrorCode.SOCIAL_ACCOUNT_ALREADY_LINKED);
        }

        socialAccountRepository.findByUserIdAndProviderName(userId, providerKey)
                .orElseGet(() -> {
                    var newEntry = UserSocialAccount.builder()
                            .user(user)
                            .providerName(providerKey)
                            .providerUserId(profile.providerId())
                            .build();
                    return socialAccountRepository.save(newEntry);
                });

        if (user.getAuthProvider() == AuthProvider.LOCAL) {
            user.setAuthProvider(AuthProvider.BOTH);
            userRepository.save(user);
        }

        log.info("Social account linked – user={} provider={}", user.getEmail(), providerKey);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void setPassword(String userId, SetPasswordRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getPassword() != null) {
            if (request.oldPassword() == null || !passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
                throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
            }
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            user.setAuthProvider(AuthProvider.BOTH);
        }

        userRepository.save(user);
        log.info("Password updated – user={}", user.getEmail());
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private SocialLoginProvider resolveProvider(String providerKey) {
        return Optional.ofNullable(socialLoginProviders.get(providerKey))
                .orElseThrow(() -> new AppException(ErrorCode.UNSUPPORTED_PROVIDER, providerKey));
    }

    private User processExistingUserSocialLogin(User user, String providerKey, SocialProfile profile) {
        if (user.getAuthProvider() == AuthProvider.LOCAL) {
            throw new AppException(ErrorCode.EMAIL_CONFLICT_LOCAL_ACCOUNT);
        }

        socialAccountRepository.findByUserIdAndProviderName(user.getId(), providerKey)
                .orElseGet(() -> {
                    var newEntry = UserSocialAccount.builder()
                            .user(user)
                            .providerName(providerKey)
                            .providerUserId(profile.providerId())
                            .build();
                    return socialAccountRepository.save(newEntry);
                });

        return user;
    }

    private User createSocialUser(SocialProfile profile, String providerKey) {
        var authProvider = parseAuthProvider(providerKey);
        var avatarUrl = Optional.ofNullable(profile.avatarUrl())
                .orElse("https://example.com/default-avatar.png");

        var newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email(profile.email())
                .password(null)
                .authProvider(authProvider)
                .fullName(profile.name())
                .avatarUrl(avatarUrl)
                .build();

        var savedUser = userRepository.save(newUser);

        var socialAccount = UserSocialAccount.builder()
                .user(savedUser)
                .providerName(providerKey)
                .providerUserId(profile.providerId())
                .build();
        socialAccountRepository.save(socialAccount);

        log.info("New social user created – email={} provider={}", profile.email(), providerKey);
        return savedUser;
    }

    private AuthProvider parseAuthProvider(String providerKey) {
        try {
            return AuthProvider.valueOf(providerKey);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.UNSUPPORTED_PROVIDER, providerKey);
        }
    }
}

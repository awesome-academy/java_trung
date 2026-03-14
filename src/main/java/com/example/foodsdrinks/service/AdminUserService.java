package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.UserFilterRequest;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.repository.UserRepository;
import com.example.foodsdrinks.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> getUsers(UserFilterRequest filter, Pageable pageable) {
        Specification<User> spec = UserSpecification.filter(
                filter.getEmail(),
                filter.getRole(),
                filter.getActive()
        );
        return userRepository.findAll(spec, pageable);
    }

    public void toggleActive(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
